package com.yevhenii.cookbook.chapter2

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.pattern.{ask, pipe}
import akka.util.Timeout

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global


case object CreateChild
case object Send
case class Response(value: Int)
case class Request(x: Int)


class DoubleActor(f: Int => Int) extends Actor {
  override def receive: Receive = {
    case Request(x) =>
      println(s"I [${self.path}] received message [$x], f(x) = ${f(x)}")
      sender ! Response(f(x))
  }
}


class MyParentActor()(implicit timeout: Timeout) extends Actor {
  var children = List.empty[ActorRef]

  override def receive: Receive = {
    case CreateChild =>
      println(s"I [${self.path}] am creating a child")
      children ::= context.actorOf(Props(new DoubleActor(_ * children.size)))
    case Send =>
      println("Sending message to my children")
//      val result: Future[List[Int]] = (children zipWithIndex) map (e => e._1 ? Request(e._2)) traverse (_.asInstanceOf[Response].value)
      val result: Future[List[Any]] = (children zipWithIndex) map (e => e._1 ? Request(e._2)) traverse()
      result pipeTo sender
  }

  implicit class traversable[A](list: List[Future[A]]) {
    def defaultConverter[B](x: A): B = x.asInstanceOf[B]

    def traverse[B](f: A => B): Future[List[B]] =
      list.foldRight(Future.successful(List.empty[B])) { (elem, list) =>
        for {
          xs <- list
          x <- elem
        } yield f(x) :: xs
      }

    def traverse(): Future[List[A]] =
      list.foldRight(Future.successful(List.empty[A])) { (elem, list) =>
        for {
          xs <- list
          x <- elem
        } yield x :: xs
      }

    //    def traverse[B](f: A => B): Future[List[B]] =
    //      (list foldRight Future.successful(List.empty[B])) { (elem, list) =>
    //        list flatMap (xs => elem map (x => f(x) :: xs))
    //      }
  }
}


class MyOtherParentActor() extends Actor {
  val random = new java.security.SecureRandom()
  var children = List.empty[ActorRef]


  override def receive: Receive = {
    case CreateChild =>
      println(s"I [${self.path}] am creating a child")
      children ::= context.actorOf(Props(new DoubleActor(_ * children.size)))

    case Send =>
      println("Sending message to my children")
      (children zipWithIndex) foreach (e => e._1 ! Request(e._2))

    case Response(x) =>
      println(s"I [${self.path}] received response [$x] from [${sender.path}]")
  }
}


object SendMessagesToChildren extends App {

  val system = ActorSystem("ChildrenSystem")
  implicit val timeout: Timeout = 10 seconds
  val parent = system.actorOf(Props(new MyParentActor()))

  parent ! CreateChild
  parent ! CreateChild
  parent ! CreateChild
  parent ! CreateChild

  parent ? Send foreach {
    case x: Future[List[_]] =>
      x foreach (_ foreach println)
    case x =>
      println(x)
  }

  val parent2 = system.actorOf(Props(new MyOtherParentActor()))

  parent2 ! CreateChild
  parent2 ! CreateChild
  parent2 ! CreateChild
  parent2 ! CreateChild

  parent2 ! Send
}
