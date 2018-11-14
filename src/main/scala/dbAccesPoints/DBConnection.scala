package dbAccesPoints

import com.typesafe.config.{Config, ConfigFactory}
import reactivemongo.api.{DefaultDB, MongoConnection, MongoDriver}

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait DBConnection {

    val config: Config = ConfigFactory.load("resources/application.conf")
    val database: String = config.getString("mongodb.database")
    val servers: mutable.Buffer[String] = config.getStringList("mongodb.servers").asScala

    val driver = MongoDriver()
    val connection: MongoConnection = driver.connection(servers)
    val db: Future[DefaultDB] = connection.database(database)
}
