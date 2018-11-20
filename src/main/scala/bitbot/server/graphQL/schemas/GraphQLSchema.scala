package bitbot.server.graphQL.schemas

import akka.http.scaladsl.model.DateTime
import bitbot.server.graphQL.contexts.DashboardContext
import bitbot.server.graphQL.models.NoAuthorized
import javax.annotation.Resource.AuthenticationType
import sangria.schema.{ListType, ObjectType}
import sangria.ast.StringValue
import sangria.execution.deferred.{DeferredResolver, Fetcher, Relation, RelationIds}
import sangria.schema._
import sangria.macros.derive._

object GraphQLSchema {
  

  import GraphQLTypes._
  val NameArg = Argument("name", StringType)
  val UrlArg = Argument("url", StringType)
  val DescArg = Argument("description", StringType)
  val PostedByArg = Argument("postedById", IntType)
  val LinkIdArg = Argument("linkId", IntType)
  val UserIdArg = Argument("userId", IntType)
  val LoginArg = Argument("login", StringType)
  val PasswordArg = Argument("password", StringType)
  
  val QueryType = ObjectType(
    "Query",
    fields[DashboardContext, Unit](
      Field("dumb", StringType, resolve = c => "hello")
    )
  )
  
  val Mutation = ObjectType(
    "Mutation",
    fields[DashboardContext, Unit](
      Field("logIn",
        AuthorizationType,
        arguments = LoginArg :: PasswordArg :: Nil,
        tags = NoAuthorized :: Nil,
        resolve = ctx => UpdateCtx(
          ctx.ctx.login(ctx.arg(LoginArg), ctx.arg(PasswordArg))) {
            token => ctx.ctx.copy(maybeToken = Some(token))
        }
      )
    )
  )
  
  val SchemaDefinition = Schema(QueryType, Some(Mutation))
}
