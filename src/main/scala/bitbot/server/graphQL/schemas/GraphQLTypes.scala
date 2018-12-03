package bitbot.server.graphQL.schemas


import bitbot.server.Documents._
import bitbot.server.graphQL.models.AuthorizationToken
import sangria.macros.derive._
import sangria.schema.{ObjectType, ScalarType}

object GraphQLTypes {

  import GraphQLConversions.GraphQLDate

  implicit val AuthorizationType: ObjectType[Unit, AuthorizationToken] =
    deriveObjectType[Unit, AuthorizationToken]()
  
  implicit val AdminType: ObjectType[Unit, Admin] = deriveObjectType[Unit, Admin]()
  
  implicit val EventType: ObjectType[Unit, Event] = deriveObjectType[Unit, Event]()
  
  implicit val TalkerType: ObjectType[Unit, Talker] = deriveObjectType[Unit, Talker]()
  
  implicit lazy val AnswerType: ObjectType[Unit, Answer] = deriveObjectType[Unit, Answer]()
  
  implicit lazy val AnswerVerificationType: ObjectType[Unit, AnswerVerification] = deriveObjectType[Unit, AnswerVerification]()


}
