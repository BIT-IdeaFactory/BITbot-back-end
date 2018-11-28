package bitbot.server.dbEntities

case class AnswerVerification(verificator: Talker, verdict: Boolean, correctAnswer: Option[String])
