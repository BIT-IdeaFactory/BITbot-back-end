package dbEntities

import com.typesafe.config.{Config, ConfigFactory}
import reactivemongo.api.{DefaultDB, MongoConnection, MongoDriver}

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

trait DBConnectionProps {
    
    val config: Config = ConfigFactory.load("resources/application.conf")
    val serversUri: String = config.getString("mongodb.servers-uri")

    val driver: MongoDriver = MongoDriver()


    val database: Future[DefaultDB] = for {
      uri <- Future.fromTry(MongoConnection.parseURI(serversUri))
      con = driver.connection(uri)
      dbName <- Future(uri.db.get)
      db <- con.database(dbName)
    } yield db

}

