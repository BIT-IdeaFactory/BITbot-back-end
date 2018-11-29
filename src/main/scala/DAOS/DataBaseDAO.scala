package DAOS

import Documents._
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson.BSONDocumentWriter

import scala.concurrent.Future

trait DataBaseDAO {
    def insertDocument[T <: Document](document: T)(implicit writer: BSONDocumentWriter[T]): Future[WriteResult]

    def getAdminUser(password: String, login: String): Future[Option[Admin]]

    def getAllEvents: Future[List[Event]]

    def getEvent(name: String): Future[Option[Event]]

    def getTalkers(firstName: String, lastName: String): Future[List[Talker]]

    def getTalker(fbId: String): Future[Option[Talker]]

    def getAllAnswer: Future[List[Answer]]

    def getTalkerAnswer(fbId: String): Future[List[Answer]]

    def updateAnswer(answer: Answer, answerVerification: AnswerVerification): Future[WriteResult]
}