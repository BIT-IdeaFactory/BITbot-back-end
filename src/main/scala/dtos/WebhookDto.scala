package dtos

import spray.json.DefaultJsonProtocol

package object WebhookDto {
  case class Sender(id: String)
  case class Recipient(id: String)
  case class Message(text: String)
  case class Messaging(sender: Sender, recipient: Recipient, message: Message)
  case class Entry(id: String, time: Long, messaging: List[Messaging])
  case class Webhook(`object`: String, entry: List[Entry])

  object MessagingProtocol extends DefaultJsonProtocol {
    implicit val senderFormat = jsonFormat1(Sender)
    implicit val recipientFormat = jsonFormat1(Recipient)
    implicit val messageFormat = jsonFormat1(Message)
    implicit val messagingFormat = jsonFormat3(Messaging)
    implicit val entryFormat = jsonFormat3(Entry)
    implicit val webhookFormat = jsonFormat2(Webhook)
  }
}
