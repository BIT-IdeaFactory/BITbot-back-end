package bitbot.server.actors

import java.sql.Time
import java.util.{Calendar, Date}

import akka.actor.{Actor, ActorLogging, Props}
import bitbot.server.actors.AuthorisationActor.{AuthFailed, AuthOK, AuthoriseMessage, LogInMessage}
import bitbot.server.graphQL.models.AuthorizationToken
object AuthorisationActor {
  def props(): Props = Props(new AuthorisationActor)
  
  case class LogInMessage(login: String, password: String)
  case class AuthoriseMessage(token: AuthorizationToken)
  case class AuthResponse()
  case object AuthOK
  case object AuthFailed
}

class AuthorisationActor extends Actor with ActorLogging {
  
  private val activeTokens: Map[String, Time] = Map.empty[String, Time]
  
  override def receive: Receive = {
    case LogInMessage(login, password) =>  handleLogin(login, password)
    case AuthoriseMessage(AuthorizationToken(token)) => handleAuthorizationCheck(token)
    
  }
  
  private def handleLogin(login: String, passwd: String): Unit = {
    if (credentialsValid(login, passwd)){
      val time = Calendar.getInstance()
      time.add(Calendar.MINUTE, 20)
      activeTokens + ("abcd" -> time.getTime)
      sender() ! Some(AuthorizationToken("abcd"))
    } else {
      sender() ! None
    }
  }
  
  private def credentialsValid(login: String, passwd: String): Boolean = {
    login == "user" && passwd == "password"
  }
  
  private def handleAuthorizationCheck(token: String): Unit = {
    if (isAuthorised(token)) {
      updateSessionTime(token)
      sender() ! AuthOK
    } else {
      sender() ! AuthFailed
    }
  }
  
  private def isAuthorised(token: String): Boolean = {
    activeTokens.get(token).exists(time => time.after(new Date()))
  }
  
  private def updateSessionTime(token: String): Unit = {
    val time = Calendar.getInstance
    time.add(Calendar.MINUTE, 20)
    activeTokens + (token -> time.getTime)
  }
}
