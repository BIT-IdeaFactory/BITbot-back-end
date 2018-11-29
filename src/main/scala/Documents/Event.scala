package Documents

import java.util.Date

import reactivemongo.bson.{BSONDocumentHandler, Macros}

case class Event(name: String, date: Date, eventLink: String) extends Document

object Event {
    implicit val eventBSONHandler: BSONDocumentHandler[Event] = Macros.handler[Event]
}
