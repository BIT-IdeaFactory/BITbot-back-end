package bitbot.server.graphQL.schemas

import bitbot.server.graphQL.contexts.DashboardContext
import bitbot.server.graphQL.models.{Authorized, NoAuthorized}
import sangria.schema.{Field, ListType, ObjectType, _}

object GraphQLSchema {


  import GraphQLTypes._

  val LoginArg = Argument("login", StringType)
  val PasswordArg = Argument("password", StringType)
  val NameArg = Argument("name", StringType)
  val TalkerIdArg = Argument("fbID", StringType)
  val SurnameArg = Argument("surname", StringType)
  val EmailArg = Argument("email", StringType)
  val LinkArg = Argument("link", StringType)

  val QueryType = ObjectType(
    "Query",
    fields[DashboardContext, Unit](
      Field(
        "allEvents",
        ListType(EventType),
        tags = Authorized :: Nil,
        resolve = c => c.ctx.DAO.getAllEvents
      ),
      Field(
        "event",
        OptionType(EventType),
        tags = Authorized :: Nil,
        arguments = NameArg :: Nil,
        resolve = c => c.ctx.DAO.getEvent(c.arg(NameArg))
      ),
      Field(
        "talker",
        OptionType(TalkerType),
        arguments = TalkerIdArg :: Nil,
        tags = Authorized :: Nil,
        resolve = c => c.ctx.DAO.getTalker(c.arg(TalkerIdArg))
      ),
      Field(
        "talkers",
        ListType(TalkerType),
        tags = Authorized :: Nil,
        arguments = NameArg :: SurnameArg :: Nil,
        resolve = c => c.ctx.DAO.getTalkers(c.arg(NameArg), c.arg(SurnameArg))
      ),
      Field(
        "answers",
        ListType(AnswerType),
        tags = Authorized :: Nil,
        resolve = c => c.ctx.DAO.getAllAnswer
      ),
      Field(
        "talkerAnswers",
        ListType(AnswerType),
        tags = Authorized :: Nil,
        arguments = TalkerIdArg :: Nil,
        resolve = c => c.ctx.DAO.getTalkerAnswer(c.arg(TalkerIdArg))
      )
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
      ),
      // Not yet implemented
      Field("createAdmin",
        StringType,
        arguments = LoginArg :: PasswordArg :: EmailArg :: Nil,
        tags = Authorized :: Nil,
        resolve = ctx => "Created admin " + ctx.arg(LoginArg)
      ),
      // Not yet implemented
      Field("deleteAdmin",
        StringType,
        arguments = LoginArg :: Nil,
        tags = Authorized :: Nil,
        resolve = ctx => "Deleted admin " + ctx.arg(LoginArg)
      ),
      // Not yet implemented
      Field("updateAdmin",
        StringType,
        tags = Authorized :: Nil,
        arguments = LoginArg :: PasswordArg :: EmailArg :: Nil,
        resolve = ctx => "Updated admin " + ctx.arg(LoginArg)
      ),
      // Not yet implemented
      Field("addEvent",
        StringType,
        tags = Authorized :: Nil,
        arguments = NameArg :: LinkArg :: Nil,
        resolve = ctx => "Created event " + ctx.arg(NameArg)
      ),
      // Not yet implemented
      Field("updateEvent",
        StringType,
        tags = Authorized :: Nil,
        arguments = NameArg :: LinkArg :: Nil,
        resolve = ctx => "Updated event " + ctx.arg(NameArg)
      ),
      // Not yet implemented
      Field("deleteEvent",
        StringType,
        tags = Authorized :: Nil,
        arguments = NameArg :: LinkArg :: Nil,
        resolve = ctx => "Deleted event " + ctx.arg(NameArg)
      )
    )
  )

  val SchemaDefinition = Schema(QueryType, Some(Mutation))
}
