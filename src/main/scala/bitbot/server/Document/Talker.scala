package bitbot.server.Document

import reactivemongo.bson.{BSONDocumentHandler, Macros}

case class Talker(fBId: String, firstName: String, lastName: String) extends Document

object Talker {
    implicit val talkerBSONHandler: BSONDocumentHandler[Talker] = Macros.handler[Talker]
}
