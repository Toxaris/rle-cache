// RLE cache microservice
// Copyright 2016 Tillmann Rendel.
// All Rights Reserved.

package rlecache

import akka.actor._

object Cacher {
  /** Message to cacher: Find cached item at specified index. */
  case class Get(index: Int)

  /** Message to cacher: Cache this sequence. */
  case class Put(seq: Seq[String])

  /** Return props for creating a Cacher. */
  def props: Props = Props[Cacher]
}

/** Actor that fetches data from upstream. */
class Cacher extends Actor with ActorLogging {
  // import messages
  import Cacher._

  // initialize cache
  var cache = Seq[String]()

  // process messages
  def receive = {
    case Put(seq) =>
      cache = seq
      log.info("Cached {} items.", seq.size)
    case Get(index) =>
      sender ! cache.lift(index)
  }
}
