package bitbot.server.graphQL.middlewares

import bitbot.server.graphQL.contexts.DashboardContext
import bitbot.server.graphQL.models.{Authorized, NoAuthorized}
import sangria.execution.{Middleware, MiddlewareBeforeField, MiddlewareQueryContext}
import sangria.schema.Context

object AuthMiddleware extends Middleware[DashboardContext] with MiddlewareBeforeField[DashboardContext] {
  override type QueryVal = Unit
  override type FieldVal = Unit

  override def beforeQuery(context: MiddlewareQueryContext[DashboardContext, _, _]) = ()

  override def afterQuery(queryVal: QueryVal, context: MiddlewareQueryContext[DashboardContext, _, _]) = ()

  override def beforeField(queryVal: QueryVal, mctx: MiddlewareQueryContext[DashboardContext, _, _], ctx: Context[DashboardContext, _]) = {
    val requireAuth = ctx.field.tags contains Authorized
    val unAuthenticatedOnly = ctx.field.tags contains NoAuthorized
    if (requireAuth) ctx.ctx.ensureAuthenticated()
    if (unAuthenticatedOnly) ctx.ctx.ensureUnauthenticated()
    continue
  }
}