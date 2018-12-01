package services

import akka.actor.{Actor, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import configurations.BitBotConfig
import services.MessageTransmitter.SendMessage
import spray.json._

object MessageTransmitter {
  case class SendMessage(recipientId:String, text:String)
  def props() = Props[MessageTransmitter]
}

class MessageTransmitter extends Actor {
  override def receive: Receive = {
    case SendMessage(recipient, text) =>
      handleSendMessage(recipient, text)
  }

  private def handleSendMessage(recipientId: String, text: String): Unit = {
    println("Received MessageProcessed")

    Http(context.system).singleRequest(
      HttpRequest(
        HttpMethods.POST,
        getFbUri(),
        entity = HttpEntity(ContentTypes.`application/json`, getBody(recipientId, text))
      )
    )
  }

  private def getFbUri(): Uri = {
    Uri(BitBotConfig.fb.messagesEndpoint)
      .withQuery(
        Uri.Query(
          "access_token" -> BitBotConfig.fb.pageAccessToken
        )
      )
  }

  private def getBody(recipientId: String, text: String): String = {
    case class Message(text: String)
    case class Recipient(id: String)
    case class RequestBody(messaging_type: String, recipient: Recipient, message: Message)
    object Protocol extends DefaultJsonProtocol {
      implicit val messageFormat = jsonFormat1(Message)
      implicit val recipient = jsonFormat1(Recipient)
      implicit val requestBodyFormat = jsonFormat3(RequestBody)
    }
    import Protocol._
    RequestBody("RESPONSE", Recipient(recipientId), Message(text))
      .toJson
      .toString()
  }
}
