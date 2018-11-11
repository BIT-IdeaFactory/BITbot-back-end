package dbEntities

import akka.util.Timeout
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.{BSON, BSONDocument}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object DBConnector extends DBConnectionProps {
    implicit val timeout: Timeout = 5.seconds
    
    def test(): Unit = {
        println("opening connection to database")
        
        val basicDataField: BasicDataField = BasicDataField("Czy ?", "Tak", "Józio")
        val collection = db.map(_.collection[BSONCollection]("gatheredData"))
        
        collection.map(_.insert(BSON.write(basicDataField)))
        val result = collection.flatMap(_.find(BSONDocument("userName" → "Józio")).one)
        
        result.onSuccess {
            case Some(doc) ⇒ print(BSON.readDocument[BasicDataField](doc).userName)
            case None ⇒ println("got nothing")
        }
    }
}
