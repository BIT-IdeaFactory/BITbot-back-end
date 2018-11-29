package DAOS

import Documents._
import cats.Functor
import cats.instances.all._
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
    val functorFutureOption: Functor[Future[Option[α$$]] forSome {type α$$}] = Functor[Future] compose Functor[Option]

    val connection: MongoConnection = driver.connection(servers)
    val db: Future[DefaultDB] = connection.database(database)
    val collections: Map[String, Future[BSONCollection]] =
        config.getStringList("collectionName.all")
            .asScala.toList
            .map(
                collectionName =>
                    collectionName -> db.map(_.collection[BSONCollection](collectionName))
            ).toMap


    override def insertDocument[T <: Document](document: T)(implicit writer: BSONDocumentWriter[T]): Future[WriteResult] = {
        val collection = collections(Document.getCollectionName(document.getClass))
        val bsonForm = BSON.writeDocument(document)
        val writeRes: Future[WriteResult] = collection.flatMap(_.update(bsonForm, bsonForm, upsert = true))
        writeRes.onComplete {
            case Failure(e) => e.printStackTrace()
            case Success(writeResult) =>
                println(s"successfully inserted document with result: $writeResult")
        }
        writeRes
    }

    override def getAdminUser(password: String, login: String): Future[Option[Admin]] = {
        val request = BSONDocument("password" -> password, "login" -> login)
        getOne[Admin](request)
    }

    override def getAllEvents: Future[List[Event]] = {
        getMany[Event](BSONDocument.empty)
    }

    override def getEvent(name: String): Future[Option[Event]] = {
        val request = BSONDocument("name" -> name)
        getOne[Event](request)
    }

    private def getOne[T <: Document](request: BSONDocument)(implicit reader: BSONDocumentReader[T]): Future[Option[T]] = {
        val collection: Future[BSONCollection] = collections(Document.getCollectionName(classOf[T]))
        functorFutureOption.map(collection.flatMap(_.find(request).one))(BSON.readDocument[T](_))
    }

    override def getTalkers(firstName: String, lastName: String): Future[List[Talker]] = {
        val request = BSONDocument("firstName" -> firstName, "lastName" -> lastName)
        getMany[Talker](request)
    }

    override def getTalker(fbId: String): Future[Option[Talker]] = {
        val request = BSONDocument("fbId" -> fbId)
        getOne[Talker](request)
    }

    override def getTalkerAnswer(fbId: String): Future[List[Answer]] = ???

    override def updateAnswer(answer: Answer, answerVerification: AnswerVerification): Future[WriteResult] = ???

    //TODO this is not working properly
    override def getAllAnswer: Future[List[Answer]] = {
        getMany[Answer](BSONDocument.empty)
    }

    private def getMany[T <: Document](request: BSONDocument)(implicit reader: BSONDocumentReader[T]): Future[List[T]] = {
        val collection: Future[BSONCollection] = collections(Document.getCollectionName(classOf[T]))
        collection.flatMap(_.find(BSONDocument.empty).cursor[T]().collect[List](-1, Cursor.FailOnError()))
    }

}

object MongoDBDAO {
    def apply(config: Config): MongoDBDAO = new MongoDBDAO(config)
}
