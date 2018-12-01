package DAOS

import Documents._
import cats.Functor
import cats.implicits._
import com.typesafe.config.Config
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.commands.WriteResult
import reactivemongo.api.{Cursor, DefaultDB, MongoConnection, MongoDriver}
import reactivemongo.bson.{BSON, BSONDocument, BSONDocumentReader, BSONDocumentWriter}

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class MongoDBDAO(val config: Config) extends DataBaseDAO {
    val driver = MongoDriver()
    val database: String = config.getString("mongodb.database")
    val servers: List[String] = config.getStringList("mongodb.servers").asScala.toList
    type FutureOption[A] = Future[Option[A]]

    val connection: MongoConnection = driver.connection(servers)
    val db: Future[DefaultDB] = connection.database(database)
    val collections: Map[String, Future[BSONCollection]] =
        config.getStringList("collectionName.all")
            .asScala
            .toList
            .map(
                collectionName =>
                    collectionName -> db.map(_.collection[BSONCollection](collectionName))
            ).toMap

    override def getAdminUser(password: String, login: String): Future[Option[Admin]] = {
        val request = BSONDocument("password" -> password, "login" -> login)
        getOne(request, classOf[Admin])
    }

    override def getAllEvents: Future[List[Event]] = {
        val request = BSONDocument.empty
        getMany(request, classOf[Event])
    }

    override def getEvent(name: String): Future[Option[Event]] = {
        val request = BSONDocument("name" -> name)
        getOne(request, classOf[Event])
    }

    override def getTalkers(firstName: String, lastName: String): Future[List[Talker]] = {
        val request = BSONDocument("firstName" -> firstName, "lastName" -> lastName)
        getMany(request, classOf[Talker])
    }

    private def getMany[T <: Document](request: BSONDocument, classType: Class[T])(implicit reader: BSONDocumentReader[T]): Future[List[T]] = {
        val collection: Future[BSONCollection] = collections(Document.getCollectionName(classType))
        collection.flatMap(_.find(request, None).cursor[T]().collect[List](-1, Cursor.FailOnError()))
    }

    override def getTalker(fbId: String): Future[Option[Talker]] = {
        val request = BSONDocument("fbId" -> fbId)
        getOne(request, classOf[Talker])
    }

    private def getOne[T <: Document](request: BSONDocument, classType: Class[T])(implicit reader: BSONDocumentReader[T]): Future[Option[T]] = {
        val collection: Future[BSONCollection] = collections(Document.getCollectionName(classType))
        collection.flatMap(_.find(request, None).one)
    }

    override def getAllAnswer: Future[List[Answer]] = {
        val request = BSONDocument.empty
        getMany(request, classOf[Answer])
    }

    override def getTalkerAnswer(fbId: String): Future[List[Answer]] = {
        val request = BSONDocument("talker.fBId" -> fbId)
        getMany(request, classOf[Answer])
    }

    override def updateAnswer(answer: Answer, answerVerification: AnswerVerification): Future[WriteResult] = {
        val request = BSON.writeDocument(answer)
        val toUpdate: Future[Option[Answer]] = getOne(request, classOf[Answer])
        val functor = Functor[Future].compose[Option]
        functor.map(toUpdate)(update => {
            val updatedVerification = update.verifications match {
                case None => Some(List(answerVerification))
                case l@Some(_) => l.map(_ ++ List(answerVerification))
            }
            val updateNew: Answer = update.copy(verifications = updatedVerification)
            insertDocument(update, Some(updateNew))
        }).flatMap { case Some(wr) => wr }
    }

    override def insertDocument[T <: Document](document: T, update: Option[T] = Option.empty[T])(implicit writer: BSONDocumentWriter[T]): Future[WriteResult] = {
        val collection = collections(Document.getCollectionName(document.getClass))
        val bsonForm = BSON.writeDocument(document)
        val updateDoc: BSONDocument = update match {
            case None => bsonForm
            case Some(u) => BSON.writeDocument(u)
        }
        val writeRes: Future[WriteResult] = collection.flatMap(_.update(bsonForm, updateDoc, upsert = true))
        writeRes.onComplete {
            case Failure(e) => e.printStackTrace()
            case Success(writeResult) =>
                println(s"successfully inserted document with result: $writeResult")
        }
        writeRes
    }

}

object MongoDBDAO {
    def apply(config: Config): MongoDBDAO = new MongoDBDAO(config)
}
