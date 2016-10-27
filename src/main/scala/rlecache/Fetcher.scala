// RLE cache microservice
// Copyright 2016 Tillmann Rendel.
// All Rights Reserved.

package rlecache

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.Props

object Fetcher {
  /** Message to Fetcher: Fetch data from upstream now. */
  object Fetch

  /**	Return props for creating a Fetcher. */
  def props: Props = Props[Fetcher]
}

/** Actor that fetches data from upstream. */
class Fetcher extends Actor with ActorLogging {
  import Fetcher._
  def receive = {
    case Fetch => log.info("fetch data from upstream")
  }
}
