package com.yevhenii.cookbook.chapter1

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.yevhenii.cookbook.chapter1.Messages.{Done, GiveMeRandomNumber, Start}

import scala.util.Random


object Messages {
  case class Done(randomNumber: Int)
  case object GiveMeRandomNumber
  case class Start(actorRef: ActorRef)
}


class RandomNumberGenerator extends Actor {
  override def receive: Receive = {
    case GiveMeRandomNumber =>
      println("Randomize number")
      val num = Random.nextInt
      sender ! Done(num)
  }
}


class QueryActor extends Actor {
  override def receive: Receive = {
    case Start(actorRef) =>
      println("Send me next random number")
      actorRef ! GiveMeRandomNumber

    case Done(number) =>
      println(s"Received random number $number")
  }
}


object Communication extends App {

  val system = ActorSystem("HelloSystem")
  val generator = system.actorOf(Props[RandomNumberGenerator])
  val queryActor = system.actorOf(Props[QueryActor])

  queryActor ! Start(generator)
}
