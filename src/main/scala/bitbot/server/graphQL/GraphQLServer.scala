package bitbot.server.graphQL

import akka.http.scaladsl.server.Route
import sangria.parser.QueryParser
import spray.json.{JsObject, JsString, JsValue}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}
import sangria.ast.Document
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import bitbot.server.dbAccesPoints.MockDataBase
import bitbot.server.graphQL.models.{AuthenticationException, AuthorizationException, AuthorizationToken}
import bitbot.server.graphQL.schemas.GraphQLSchema
import sangria.marshalling.sprayJson._
import sangria.execution.{ExceptionHandler => EHandler, _}
import bitbot.server.graphQL.contexts.DashboardContext
import bitbot.server.graphQL.middlewares.AuthMiddleware

object GraphQLServer {
  
  val ErrorHandler = EHandler {
    case (_, AuthenticationException(message)) ⇒ HandledException(message)
    case (_, AuthorizationException(message)) ⇒ HandledException(message)
  }
  
  def endpoint(requestJSON: JsValue, token: Option[AuthorizationToken])(implicit ec: ExecutionContext): Route = {
    val JsObject(fields) = requestJSON
    val JsString(query) = fields("query")
    QueryParser.parse(query) match {
      case Success(queryAst) =>
        val operation = fields.get("operationName") collect {
          case JsString(op) => op
        }
        val variables = fields.get("variables") match {
          case Some(obj: JsObject) => obj
          case _ => JsObject.empty
        }
        complete(executeGraphQLQuery(queryAst, token, operation, variables))
      case Failure(error) =>
        complete(BadRequest, JsObject("error" -> JsString(error.getMessage)))
    }
  }
  
  private def executeGraphQLQuery(query: Document, token: Option[AuthorizationToken], operation: Option[String], vars: JsObject)(implicit ec: ExecutionContext) = {
    Executor.execute(
      GraphQLSchema.SchemaDefinition,
      query,
      DashboardContext(new MockDataBase, token),
      variables = vars,
      operationName = operation,
      exceptionHandler = ErrorHandler,
      middleware = AuthMiddleware :: Nil
    ).map(OK -> _)
      .recover {
        case error: QueryAnalysisError => BadRequest -> error.resolveError
        case error: ErrorWithResolver => InternalServerError -> error.resolveError
      }
  }
  
}
