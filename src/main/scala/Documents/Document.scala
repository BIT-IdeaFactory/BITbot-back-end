package Documents

import com.typesafe.config.{Config, ConfigFactory}

trait Document

object Document {
    private val config: Config = ConfigFactory.load("resources/application.conf")

    def getCollectionName[T](classType: Class[T]): String = {
        config.getString(s"collectionName.${classType.getSimpleName.toLowerCase()}")
    }
}