package com.yevhenii.cookbook.chapter1

import akka.actor.{Actor, ActorSystem, Props}


class BecomeUnbecomeActor extends Actor {
  override def receive: Receive = {
    case true => context become trueBehaviour
    case false => context become falseBehaviour
    case _ => println("ERROR")
  }

  def trueBehaviour: Receive = {
    case msg: String => println(s"true {$msg}")
    case false => context become falseBehaviour
    case _ => println("ERROR")
  }

  def falseBehaviour: Receive = {
    case msg: Int => println(s"fal {$msg}")
    case true => context become trueBehaviour
    case _ => println("ERROR")
  }
}

object BecomeUnbecome extends App {

  val system = ActorSystem("HelloWorld")
  val actor = system.actorOf(Props[BecomeUnbecomeActor])

  actor ! true
  actor ! "hello"
  actor ! "how are you?"
  actor ! false
  actor ! 100
  actor ! "invalid message"
  actor ! true
  actor ! "O.K."
}
