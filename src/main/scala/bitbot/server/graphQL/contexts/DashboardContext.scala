package bitbot.server.graphQL.contexts


import bitbot.server.dbAccesPoints.DataBaseAccessPoint
import bitbot.server.graphQL.models._
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

case class DashboardContext(DAO: DataBaseAccessPoint, maybeToken: Option[AuthorizationToken] = None) {
  
  def login(login: String, passwd: String): AuthorizationToken = {
    // login mock
    Await.result(DAO.getAdminUser(login, passwd), Duration.Inf)
    AuthorizationToken(Jwt.encode(
          // 20 minutes
          JwtClaim().+("user", login).issuedNow.expiresIn(1200),
          "secretKey",
          JwtAlgorithm.HS512
    ))
  }
  
  def ensureAuthenticated(): Unit = {
    maybeToken match {
      case None => throw AuthorizationException("Unauthorized operation. Please log in.")
      case Some(AuthorizationToken(token)) => validateToken(token)
    }
  }
  
  def ensureUnauthenticated(): Unit = {
    if(maybeToken.isDefined) {
      throw AuthorizationException("Already loggedIn!")
    }
  }
  
  def validateToken(token: String): Unit = {
    if(!Jwt.isValid(token, "secretKey", JwtAlgorithm.allHmac())){
      throw AuthenticationException("Token invalid!")
    }
  }
  
}
