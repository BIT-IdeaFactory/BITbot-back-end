package services
import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import configurations.BitBotConfig

object FbVerifier {
  private final val verifyToken:String = BitBotConfig.fb.verifyToken
  def verify(token:String,mode:String,challenge:String):(StatusCode,Option[String]) = {
    println(s"Verify ${token} ${mode} ${challenge}")

    if(mode !="subscribe" || token != this.verifyToken)
      return (StatusCodes.Forbidden,None)

    return (StatusCodes.OK,Option(challenge))
  }

}
