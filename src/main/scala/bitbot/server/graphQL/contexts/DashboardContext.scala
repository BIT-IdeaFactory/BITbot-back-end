package bitbot.server.graphQL.contexts

import akka.actor.ActorRef
import akka.pattern.ask
import bitbot.server.actors.{AuthorisationActor, DBAccessActor}
import bitbot.server.graphQL.models._

import scala.concurrent.duration._
import akka.util.Timeout
import bitbot.server.actors.AuthorisationActor.AuthResponse

import scala.concurrent.{Await, Future}

case class DashboardContext(dbActor: ActorRef,
                       authActor: ActorRef,
                       maybeToken: Option[AuthorizationToken] = None) {
  implicit val timeout: Timeout = Timeout(30 seconds)
  
  def login(login: String, passwd: String): AuthorizationToken = {
    import AuthorisationActor.LogInMessage
    val futureMaybeRes: Future[Option[AuthorizationToken]] =
      (authActor ? LogInMessage(login, passwd)).mapTo[Option[AuthorizationToken]]
    val maybeRes = Await.result(futureMaybeRes, Duration.Inf)
    maybeRes.getOrElse(
      throw AuthenticationException("email or password are incorrect!")
    )
  }
  
  def ensureAuthenticated(): Unit = {
    val token = maybeToken.getOrElse(
      throw AuthorizationException("Unauthorized operation. Please log in.")
    )
    import AuthorisationActor.AuthoriseMessage
    val futureMaybeRes: Future[Option[AuthResponse]] =
      (authActor ? AuthoriseMessage(token)).mapTo[Option[AuthResponse]]
    val maybeRes = Await.result(futureMaybeRes, Duration.Inf)
    maybeRes.getOrElse(
      throw AuthenticationException("email or password are incorrect!")
    )
  }
  
  def ensureUnauthenticated(): Unit = {
    if(maybeToken.isDefined) {
      throw AuthorizationException("Already loggedIn!")
    }
  }
  
}
