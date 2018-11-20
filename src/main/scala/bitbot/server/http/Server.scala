package bitbot.server.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import spray.json._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import bitbot.server.graphQL.GraphQLServer

import scala.concurrent.Await
import com.typesafe.config.ConfigFactory

import scala.language.postfixOps


object Server extends App{
  val config = ConfigFactory.load("resources/application.conf")
  val PORT = config.getString("http.port").toInt
  
  implicit val actorSystem: ActorSystem = ActorSystem("bitbot-server")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  
  import actorSystem.dispatcher
  import scala.concurrent.duration._
  
  scala.sys.addShutdownHook(() -> shutdown())
  
  val route: Route = (post & path("graphql")) {
    entity(as[JsValue]) { requestJson =>
      GraphQLServer.endpoint(requestJson)
    }
  } ~ {
    getFromResource("resources/graphiql.html")
  }
  Http().bindAndHandle(route, "0.0.0.0", PORT)
  println(s"open a browser with URL: http://localhost:$PORT")
  
  
  def shutdown(): Unit = {
    actorSystem.terminate()
    Await.result(actorSystem.whenTerminated, 30 seconds)
  }
}
