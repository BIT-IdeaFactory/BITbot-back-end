import akka.http.scaladsl.server.HttpApp
import routing.FbRoute
object BotServer extends HttpApp with App with FbRoute {
  override protected def routes = fbRoute
  startServer("localhost",8080)
}
