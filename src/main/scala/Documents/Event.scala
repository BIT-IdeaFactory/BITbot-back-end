package Documents

import reactivemongo.bson.{BSONDateTime, BSONDocumentHandler, Macros}

case class Event(name: String, date: BSONDateTime, eventLink: String) extends Document

object Event {
    implicit val eventBSONHandler: BSONDocumentHandler[Event] = Macros.handler[Event]
}
