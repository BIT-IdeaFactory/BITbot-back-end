package Documents

import com.typesafe.config.{Config, ConfigFactory}

trait Document {
    private val config: Config = ConfigFactory.load("resources/application.conf")

    def getCollectionName: String = {
        config.getString(s"collectionName.${this.getClass.getSimpleName.toLowerCase()}")
    }
}