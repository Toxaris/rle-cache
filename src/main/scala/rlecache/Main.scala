// RLE cache microservice
// Copyright 2016 Tillmann Rendel.
// All Rights Reserved.

package rlecache

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import scala.concurrent.duration._
import scala.util.Success
import scala.util.Failure

/** Main entry point of the RLE cache service. */
object Main extends App {
  // set up akka
  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()
  val log = Logging(system, "rle-cache")

  // start actors
  val cacher = system.actorOf(Cacher.props, "cacher")
  val fetcher = system.actorOf(Fetcher.props(cacher), "fetcher")

  // start regular fetching of upstream data
  system.scheduler.schedule(0.seconds, Config.rlecache.upstream.interval, fetcher, Fetcher.Fetch)

  // endpoints we provide
  val route =
      path(IntNumber) { index =>
        get {
          complete(s"Item $index requested.")
        }
      }

  val handling = Http().bindAndHandle(route, Config.rlecache.interface, Config.rlecache.port)

  handling.onSuccess {
    case _ =>
      log.info("Listening on {}:{}", Config.rlecache.interface, Config.rlecache.port)
  }

  handling.onFailure {
    case _ =>
      system.terminate()
  }
}
