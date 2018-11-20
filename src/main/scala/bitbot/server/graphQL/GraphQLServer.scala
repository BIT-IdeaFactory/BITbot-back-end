package bitbot.server.graphQL

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Route
import sangria.parser.QueryParser
import spray.json.{JsObject, JsString, JsValue}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}
import sangria.ast.Document
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.stream.ActorMaterializer
import bitbot.server.actors.{AuthorisationActor, DBAccessActor}
import bitbot.server.graphQL.models.{AuthenticationException, AuthorizationException}
import bitbot.server.graphQL.schemas.GraphQLSchema
import sangria.marshalling.sprayJson._
import sangria.execution.{ExceptionHandler => EHandler, _}
import bitbot.server.graphQL.contexts.DashboardContext
import bitbot.server.graphQL.middlewares.AuthMiddleware

object GraphQLServer {
  
  implicit val actorSystem: ActorSystem = ActorSystem("bitbot-server")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
 
  private val authActor: ActorRef = actorSystem.actorOf(AuthorisationActor.props())
  private val dbActor: ActorRef = actorSystem.actorOf(DBAccessActor.props())
  
  val ErrorHandler = EHandler {
    case (_, AuthenticationException(message)) ⇒ HandledException(message)
    case (_, AuthorizationException(message)) ⇒ HandledException(message)
  }
  
  def endpoint(requestJSON: JsValue)(implicit ec: ExecutionContext): Route = {
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
        complete(executeGraphQLQuery(queryAst, operation, variables))
      case Failure(error) =>
        complete(BadRequest, JsObject("error" -> JsString(error.getMessage)))
    }
  }
  
  private def executeGraphQLQuery(query: Document, operation: Option[String], vars: JsObject)(implicit ec: ExecutionContext) = {
    Executor.execute(
      GraphQLSchema.SchemaDefinition,
      query,
      DashboardContext(dbActor, authActor),
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
