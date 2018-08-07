package com.yevhenii.cookbook.chapter1

import akka.actor.ActorSystem

object HelloAkkaActorSystem extends App {

  val system = ActorSystem("HelloAkka")
  println(system)
}
