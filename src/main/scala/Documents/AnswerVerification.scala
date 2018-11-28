package Documents

import reactivemongo.bson.{BSONDocumentHandler, Macros}

case class AnswerVerification(verificator: Talker, verdict: Boolean, correctAnswer: Option[String]) extends Document

object AnswerVerification {
    implicit val answerVerificationBSONHandler: BSONDocumentHandler[AnswerVerification] = Macros.handler[AnswerVerification]
}