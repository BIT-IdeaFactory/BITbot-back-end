package supervisors

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.{Backoff, BackoffSupervisor}
import dbAccesPoints.DBDocumentAccessPoint
import dbAccesPoints.DBDocumentAccessPoint.FindData
import reactivemongo.bson.BSONDocument

import scala.concurrent.duration._

class DBSupervisor(documentName: String) extends Actor with ActorLogging {
    private val dBSupervisorProps = BackoffSupervisor.props(Backoff.onFailure(
        DBDocumentAccessPoint.props(documentName),
        childName = "DBDocumentAccessPoint",
        minBackoff = 3.seconds,
        maxBackoff = 30.seconds,
        randomFactor = 0.2,
        maxNrOfRetries = 5
    ))


    private val supervisorRef = context.system.actorOf(dBSupervisorProps)

    override def receive: Receive = {
        case request: BSONDocument =>
            supervisorRef ! FindData(request)
        case _ => log.error("bad request")
    }
}

object DBSupervisor {
    def props(documentName: String): Props = Props(new DBSupervisor(documentName))
}
