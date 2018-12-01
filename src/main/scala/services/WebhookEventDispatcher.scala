package services

import akka.actor.{Actor, Props}
import dtos.WebhookDto.Webhook
import services.MessageTransmitter.SendMessage
import services.WebhookEventDispatcher.DispatchWebhook

object WebhookEventDispatcher{
  def props() = Props[WebhookEventDispatcher]
  case class DispatchWebhook(webhook:Webhook)
}
class WebhookEventDispatcher extends Actor{
  override def receive: Receive = {
    case cmd:DispatchWebhook =>
      handle(cmd)
  }
  private def handle(cmd: DispatchWebhook): Unit = cmd match{
    case DispatchWebhook(webhook) if(webhook.`object`=="page") => {
      cmd.webhook.entry.foreach{entry=>
        val messaging = entry.messaging.head
        context.system.actorOf(MessageTransmitter.props()) ! SendMessage(messaging.sender.id,messaging.message.text)
      }
    }
  }
}
