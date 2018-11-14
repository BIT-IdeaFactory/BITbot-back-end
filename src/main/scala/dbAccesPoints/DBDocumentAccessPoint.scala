package dbAccesPoints

import akka.actor.{Actor, ActorLogging, Props}
import dbEntities.BasicDataField
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.{BSON, BSONDocument}

import scala.concurrent.ExecutionContext.Implicits.global

class DBDocumentAccessPoint(documentName: String) extends DBConnection with Actor with ActorLogging {

    import DBDocumentAccessPoint._

    private val collection = db.map(_.collection[BSONCollection](documentName))

    override def preStart(): Unit = {
        log.info("opening connection to database")
    }

    override def receive: Receive = {
        case InsertData(data) => collection.map(_.insert(BSON.write(data)))

        case FindData(request) =>
            // projection allows which fields to choose
            val projection = BSONDocument("userName" -> 1, "question" -> 1, "userAnswer" -> 1)
            val results = collection.flatMap(_.find(request, projection).one)
            results foreach {
                case Some(doc) ⇒ print(BSON.readDocument[BasicDataField](doc).userName)
                case None ⇒ log.info(request.toString() + " got no results")
            }

        case _ => log.info("Request unknown")
    }
}

object DBDocumentAccessPoint {

    case class InsertData(data: BasicDataField)

    case class FindData(request: BSONDocument)

    def props(documentName: String): Props = Props(new DBDocumentAccessPoint(documentName))
}
