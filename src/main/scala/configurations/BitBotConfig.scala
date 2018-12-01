package configurations

import com.typesafe.config.ConfigFactory

object BitBotConfig {
  private val config = ConfigFactory.load()
  private val botConfig = config.getConfig("bitBot")
  object fb{
    private val fbConfig = botConfig.getConfig("fb")
    final val verifyToken = fbConfig.getString("verifyToken")
    final val pageAccessToken = fbConfig.getString("pageAccessToken")
    final val messagesEndpoint = fbConfig.getString("messagesEndpoint")
  }
}
