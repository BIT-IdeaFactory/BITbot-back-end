package bitbot.server.graphQL

import bitbot.server.dbEntities.Talker
import sangria.execution.FieldTag

package object models {
  case class AuthenticationException(message: String) extends Exception(message)
  case class AuthorizationException(message: String) extends Exception(message)
  
  case class AuthorizationToken(key: String)
  
  case object Authorized extends FieldTag
  case object NoAuthorized extends FieldTag
}
