package bitbot.server.dbAccesPoints
import bitbot.server.dbEntities._
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson.{BSONDocument, BSONDocumentWriter}

import scala.concurrent.{ExecutionContext, Future}

class MockDataBase extends DataBaseAccessPoint {
  
  private val adminCollection: List[Admin] = Admin("user", "password", "admin@admin.pl") :: Nil
  
  private val eventCollection: List[Event] =
    Event("https://facebook.com/eventHash1", "Event#1") ::
    Event("https://facebook.com/eventHash2", "Event#2") ::
    Event("https://facebook.com/eventHash3", "Event#3") :: Nil
  
  private val talkerCollection: List[Talker] =
    Talker("1", "Julian", "Carax") ::
    Talker("2", "David", "Martin") ::
    Talker("3", "Daniel", "Sempere") ::
    Talker("4", "Alicja", "Gris") :: Nil
  
  private val answerCollection: List[Answer] =
    Answer("Czy 'Cień Wiatru' jest super?", "Oczywiście!", Talker("1", "Julian", "Carax"), None) ::
    Answer("Czy zwariowałem?", "Któż to wie...", Talker("1", "Julian", "Carax"), None) :: Nil
  
  implicit val ec: ExecutionContext = ExecutionContext.global
  
  
  override def insertDocument[T <: BSONDocument](document: T)(implicit writer: BSONDocumentWriter[T]): Future[WriteResult] = ???
  
  override def getAdminUser(password: String, name: String): Future[Option[Admin]] = Future {
    adminCollection.find(a => a.password == password && a.login == name)
  }
  
  override def getAllEvents: Future[List[Event]] = Future {
    eventCollection
  }
  
  override def getEvent(name: String): Future[List[Event]] =  Future {
    eventCollection.filter(e => e.name == name)
  }
  
  override def getTalkers(firstName: String, lastName: String): Future[List[Talker]] = Future {
    talkerCollection.filter(t => t.firstName == firstName && t.lastName == lastName)
  }
  
  override def getTalker(fbId: String): Future[Talker] = Future {
    talkerCollection.filter(t => t.fbId == fbId).head
  }
  
  override def getAllAnswer: Future[List[Answer]] = Future {
    answerCollection
  }
  
  override def getTalkerAnswer(fbId: String): Future[List[Answer]] = Future {
    answerCollection.filter(a => fbId == a.talker.fbId)
  }
  
}
