package com.yevhenii.cookbook.chapter1

import akka.actor.{Actor, ActorSystem, Props}


class SummingActor extends Actor {

  var sum: Int = 0

  override def receive: Receive = {
    case x: Int =>
      sum += x
      println(s"My state is $sum")

    case _ =>
      println("Unknown message, I don't want to do anything :(")
  }
}


class SummingActorWithInitial(initial: Int) extends Actor {

  var sum: Int = initial

  override def receive: Receive = {
    case x: Int =>
      sum += x
      println(s"My state is $sum")

    case _ =>
      println("Unknown message, I don't want to do anything :(")
  }
}


object BehaviorAndState extends App {

  val system = ActorSystem("HelloSystem")
  val summingActor = system.actorOf(Props[SummingActor], "SummingActor")
//  path
  println(summingActor.path)

  val summingActor2 = system.actorOf(Props(classOf[SummingActorWithInitial], 10))
  println(summingActor2.path)

  summingActor ! 1

  Thread.sleep(3000)
  summingActor ! 1

  Thread.sleep(3000)
  summingActor ! "Hello"
}
