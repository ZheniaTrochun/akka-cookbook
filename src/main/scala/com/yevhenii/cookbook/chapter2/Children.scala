package com.yevhenii.cookbook.chapter2

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global


case class Stop(actorRef: ActorRef)
case class Greet(msg: String)
case class Create(name: String)

class ChildActor extends Actor {
  override def receive: Receive = {
    case Greet(msg) =>
      println(s"[${sender.path}] sent me [${self.path}] msg [$msg], my parent is [${context.parent}] BTW")

    case msg: String =>
      println(msg)
  }
}

class ParentActor extends Actor {
  override def receive: Receive = {
    case Stop(ref) =>
      context.stop(ref)

    case Create(name) =>
      val child = context.actorOf(Props[ChildActor], name)
      child ! Greet("hello")
      sender ! child
  }
}

object Children extends App {

  implicit val timeout: Timeout = 10 seconds
  val system = ActorSystem("System")
  val parent = system.actorOf(Props[ParentActor], "parent")

  parent ? Create("child") foreach {
    case childRef: ActorRef =>
      childRef ! "test 1"
      parent ! Stop(childRef)
      Thread.sleep(1000)
      childRef ! "test 2"
  }

}
