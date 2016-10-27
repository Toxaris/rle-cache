// RLE cache microservice
// Copyright 2016 Tillmann Rendel.
// All Rights Reserved.

package rlecache

import akka.actor.ActorSystem
import scala.concurrent.duration._

/** Main entry point of the RLE cache service. */
object Main extends App {
  // set up akka
  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher

  // start actors
  val fetcher = system.actorOf(Fetcher.props, "fetcher")

  // start regular fetching of upstream data
  system.scheduler.schedule(0.seconds, 30.seconds, fetcher, Fetcher.Fetch)
}
