package bitbot.server.Documents

import reactivemongo.bson.{BSONDocumentHandler, Macros}

case class Admin(login: String, password: String, email: String) extends Document

object Admin {
    implicit val adminBSONHandler: BSONDocumentHandler[Admin] = Macros.handler[Admin]
}
