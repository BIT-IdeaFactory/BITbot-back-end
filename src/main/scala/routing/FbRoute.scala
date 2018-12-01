package routing

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.ActorMaterializer
import services.{FbVerifier, WebhookEventDispatcher}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import services.WebhookEventDispatcher.DispatchWebhook
import dtos.WebhookDto._
import dtos.WebhookDto.MessagingProtocol._
trait FbRoute extends Directives {
  protected implicit val system = ActorSystem()
  protected implicit val materializer = ActorMaterializer()
  protected implicit val executionContext = system.dispatcher
  protected val fbRoute: Route ={
    post {
      path("webhook") {
        entity(as[Webhook]) {
          webhook => {
            log(webhook)
            system.actorOf(WebhookEventDispatcher.props()) ! DispatchWebhook(webhook)
            complete(StatusCodes.OK,"EVENT_RECEIVED")
          }
        }
      }
    } ~
    get {
      path("webhook") {
        parameters("hub.verify_token", "hub.mode", "hub.challenge") {
          (token, mode, challenge) => {
            val result = FbVerifier.verify(token, mode, challenge)
            complete(result._1 -> result._2.getOrElse(""))
          }
        }
      }
    }
  }
  private def log(webhook:Webhook): Unit ={
    import spray.json._
    println("Received new message:\n")
    println(webhook.toJson.prettyPrint)
  }
}
