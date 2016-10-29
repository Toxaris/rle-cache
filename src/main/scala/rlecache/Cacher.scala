// RLE cache microservice
// Copyright 2016 Tillmann Rendel.
// All Rights Reserved.

package rlecache

import akka.actor._

object Cacher {
  /** Message to cacher: Find cached item at specified index. */
  case class Get(index: Int)

  /** Message to cacher: Cache this sequence. */
  case class Put(seq: Seq[Compressed[String]])

  /** Return props for creating a Cacher. */
  def props: Props = Props[Cacher]
}

/** Actor that fetches data from upstream. */
class Cacher extends Actor with ActorLogging {
  // import messages
  import Cacher._

  // initialize cache
  var cache = Seq[Compressed[String]]()

  // process messages
  def receive = {
    case Put(seq) =>
      cache = seq
      log.info("Cached {} items in {} runs.", Compressor.decompressedLength(seq), seq.size)
    case Get(index) =>
      try {
        sender() ! Compressor.index(cache, index)
      } catch {
        case e: IndexOutOfBoundsException =>
          sender() ! Status.Failure(e)
      }
  }
}
