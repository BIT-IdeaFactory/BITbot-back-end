import akka.actor.ActorSystem
import reactivemongo.bson.BSONDocument
import supervisors.DBSupervisor

object Main extends App {
    println("I'm BitBot")
    val system = ActorSystem()
    val db = system.actorOf(DBSupervisor.props("gatheredData"))
    db ! BSONDocument("userName" → "Józio")
}
