package bitbot.server.actors

import akka.actor.{Actor, ActorLogging, Props}

object DBAccessActor {
  def props(): Props = Props(new DBAccessActor)
}

class DBAccessActor extends Actor with ActorLogging {
  override def receive: Receive = ???
}
