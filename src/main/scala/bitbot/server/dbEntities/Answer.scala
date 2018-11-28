package bitbot.server.dbEntities

case class Answer(question: String, answer: String, talker: Talker, verifications: Option[List[AnswerVerification]])
