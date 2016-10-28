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
  val cacher = system.actorOf(Cacher.props, "cacher")

  // start regular fetching of upstream data
  system.scheduler.schedule(0.seconds, Config.rlecache.upstream.interval, fetcher, Fetcher.Fetch)
}
