package com.yevhenii.cookbook.chapter1

import akka.actor.{Actor, ActorSystem, Props}
import akka.dispatch.{PriorityGenerator, UnboundedPriorityMailbox}
import com.typesafe.config.Config

class MyPriorityActor extends Actor {
  override def receive: Receive = {
    case x: Int => println(x)
    case x: String => println(x)
    case x: Long => println(x)
    case x => println(x)
  }
}

class MyPriorityActorMailbox(settings: ActorSystem.Settings, config: Config)
  extends UnboundedPriorityMailbox(PriorityGenerator {
    case x: Int => 1
    case x: String => 2
    case x: Long => 3
    case x => 4
  })

object MessagePriority extends App {

  val system = ActorSystem("HelloWorld")
  val myPrioActor = system.actorOf(Props[MyPriorityActor].withDispatcher("prio-dispatcher"))

  myPrioActor ! 6.0
  myPrioActor ! "Hello"
  myPrioActor ! 1
  myPrioActor ! 2.0
  myPrioActor ! "Cool"
  myPrioActor ! "Cool cool cool"
  myPrioActor ! 3
}
