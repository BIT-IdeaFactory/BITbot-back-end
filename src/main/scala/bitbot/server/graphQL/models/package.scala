package bitbot.server.graphQL

import akka.http.scaladsl.model.DateTime
import sangria.execution.FieldTag

package object models {
  case class AuthenticationException(message: String) extends Exception(message)
  case class AuthorizationException(message: String) extends Exception(message)
  
  case class AuthorizationToken(key: String)
  case class BITEvent(name: String, link: String, date: DateTime)
  case class UserQuestion(questionContent: String, response: String)
  
  case object Authorized extends FieldTag
  case object NoAuthorized extends FieldTag
}
