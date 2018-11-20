package bitbot.server.graphQL.schemas

import bitbot.server.graphQL.models.AuthorizationToken
import sangria.macros.derive.{AddFields, Interfaces, ReplaceField, deriveObjectType}
import sangria.schema.{Field, ListType, ObjectType}

object GraphQLTypes {
  implicit val AuthorizationType: ObjectType[Unit, AuthorizationToken] =
    deriveObjectType[Unit, AuthorizationToken]()
}
