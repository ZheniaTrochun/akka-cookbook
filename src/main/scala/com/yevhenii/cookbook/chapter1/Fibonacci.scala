package com.yevhenii.cookbook.chapter1

import akka.actor.{Actor, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.Await


class FibonacciActor extends Actor {
  override def receive: Receive = {
    case num: Int =>
      val fibonacci = fib(num)
      sender ! fibonacci
  }

  def fib(n: Int): Int = n match {
    case 0 | 1 =>  1
    case _ =>  fib(n - 1) + fib(n - 2)
  }
}

object Fibonacci extends App {
  implicit val timeout: Timeout = 10 second
  val system = ActorSystem("HelloSystem")
  val fibActor = system.actorOf(Props[FibonacciActor])
  val future = (fibActor ? 10).mapTo[Int]
  val number = Await.result(future, 10 seconds)
  println(number)
}
