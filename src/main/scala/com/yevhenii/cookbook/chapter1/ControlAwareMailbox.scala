package com.yevhenii.cookbook.chapter1

import akka.actor.{Actor, ActorSystem, Props}
import akka.dispatch.ControlMessage


case object MyControlMessage extends ControlMessage

class Logger extends Actor {
  override def receive: Receive = {
    case MyControlMessage => println("I have to process this message IMMEDIATELY !!!")
    case str: String => println(str)
  }
}

object ControlAwareMailbox extends App {

  val system = ActorSystem("HelloWorld")
  val actor = system.actorOf(Props[Logger].withDispatcher("control-aware-dispatcher"))

  actor ! "hello"
  actor ! "how are"
  actor ! "you?"
  actor ! MyControlMessage
}
