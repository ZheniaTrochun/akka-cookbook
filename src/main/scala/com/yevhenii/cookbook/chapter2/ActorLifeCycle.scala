package com.yevhenii.cookbook.chapter2

import akka.actor.SupervisorStrategy.{Escalate, Restart}
import akka.actor.{Actor, ActorRef, ActorSystem, OneForOneStrategy, Props, SupervisorStrategy}
import akka.util.Timeout
import akka.pattern.ask

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._


case object Error
case class Stop(actorRef: ActorRef)


class LifeCycleActor extends Actor {
  var sum = 1

  override def receive: Receive = {
    case Error => throw new ArithmeticException()
    case msg => println(s"message: $msg")
  }

  override def preStart(): Unit = println(s"preStart sum = $sum")

  override def postStop(): Unit = println(s"postStop sum = ${sum * 3}")

  override def postRestart(reason: Throwable): Unit = {
    sum *= 2
    println(s"postRestart, sum = $sum")
  }
}


class Supervisor extends Actor {

  override def supervisorStrategy: SupervisorStrategy =
    OneForOneStrategy(
      maxNrOfRetries = 10,
      withinTimeRange = 10 minutes
    ) {
      case _: ArithmeticException => Restart
      case t => super.supervisorStrategy.decider.applyOrElse(t, (_: Any) => Escalate)
    }

  override def receive: Receive = {
    case (props: Props, name: String) =>
      sender ! context.actorOf(props, name)
    case Stop(actorRef) =>
      context.stop(actorRef)
  }
}


object ActorLifeCycle extends App {

  val system = ActorSystem("Supervision")
  implicit val timeout: Timeout = 10 seconds
  val supervisor = system.actorOf(Props[Supervisor], "supervisor")

  supervisor ? (Props[LifeCycleActor], "child") foreach {
    case child: ActorRef =>
      child ! Error
      Thread.sleep(1000)
      supervisor ! Stop(child)
  }
}
