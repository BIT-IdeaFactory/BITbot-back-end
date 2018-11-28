package bitbot.server.dbAccesPoints


import bitbot.server.dbEntities.{Admin, Answer, Event, Talker}
import reactivemongo.api.BSONSerializationPack.Document
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson.{BSONDocument, BSONDocumentWriter}

import scala.concurrent.Future


trait DataBaseAccessPoint {
  def insertDocument[T <: Document](document: T)(implicit writer: BSONDocumentWriter[T]): Future[WriteResult]
  
  def getAdminUser(password: String, name: String): Future[Option[Admin]]
  
  def getAllEvents: Future[List[Event]]
  
  def getEvent(name: String): Future[List[Event]]
  
  def getTalkers(firstName: String, lastName: String): Future[List[Talker]]
  
  def getTalker(fbId: String): Future[Talker]
  
  def getAllAnswer: Future[List[Answer]]
  
  def getTalkerAnswer(fbId: String): Future[List[Answer]]
}