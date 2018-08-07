package com.yevhenii.cookbook.chapter1

import akka.actor.{Actor, ActorSystem, PoisonPill, Props}


case object Stop

class ShutdownActor extends Actor {
  override def receive: Receive = {
    case Stop =>
      println("I'm stopping")
      context.stop(self)
    case str: String =>
      println(str)
  }
}

object Shutdown extends App {

  val system = ActorSystem("HelloSystem")
  val actor1 = system.actorOf(Props[ShutdownActor], "first")
  val actor2 = system.actorOf(Props[ShutdownActor], "second")

  actor1 ! "test1"
  actor1 ! Stop
  actor1 ! "test2"

  actor2 ! "test3"
  actor2 ! PoisonPill
  actor2 ! "test4"
}