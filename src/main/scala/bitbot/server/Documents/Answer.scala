package bitbot.server.Documents

import reactivemongo.bson.{BSONDocumentHandler, Macros}

case class Answer(question: String, answer: String, talker: Talker, verifications: Option[List[AnswerVerification]]) extends Document

object Answer {
    implicit val answerBSONHandler: BSONDocumentHandler[Answer] = Macros.handler[Answer]
}
