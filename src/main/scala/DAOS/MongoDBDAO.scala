package DAOS

import Documents._
import com.typesafe.config.Config
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.commands.WriteResult
import reactivemongo.api.{DefaultDB, MongoConnection, MongoDriver}
import reactivemongo.bson.{BSON, BSONDocumentWriter}

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class MongoDBDAO(val config: Config) extends DataBaseDAO {
    val driver = MongoDriver()
    val database: String = config.getString("mongodb.database")
    val servers: List[String] = config.getStringList("mongodb.servers").asScala.toList

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
        val collection = collections(document.getCollectionName)
        val writeRes: Future[WriteResult] = collection.flatMap(_.insert(BSON.writeDocument(document)))
        writeRes.onComplete {
            case Failure(e) => e.printStackTrace()
            case Success(writeResult) =>
                println(s"successfully inserted document with result: $writeResult")
        }
        writeRes
    }

    override def getAdminUser(password: String, name: String): Future[Option[Admin]] = ???

    override def getAllEvents: Future[List[Event]] = ???

    override def getEvent(name: String): Future[List[Event]] = ???

    override def getTalkers(firstName: String, lastName: String): Future[List[Talker]] = ???

    override def getTalker(fbId: String): Future[Talker] = ???

    override def getAllAnswer: Future[List[Answer]] = ???

    override def getTalkerAnswer(fbId: String): Future[List[Answer]] = ???

    override def updateAnswer(answer: Answer, answerVerification: AnswerVerification): Future[WriteResult] = ???
}

object MongoDBDAO {
    def apply(config: Config): MongoDBDAO = new MongoDBDAO(config)
}
