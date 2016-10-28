// RLE cache microservice
// Copyright 2016 Tillmann Rendel.
// All Rights Reserved.

package rlecache

import akka.actor._
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.client._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.util.ByteString

object Fetcher {
  /** Message to Fetcher: Fetch data from upstream now. */
  object Fetch

  /** Return props for creating a Fetcher. */
  def props(cacher: ActorRef): Props = Props(new Fetcher(cacher))
}

/** Actor that fetches data from upstream. */
class Fetcher(cacher: ActorRef) extends Actor with ActorLogging {
  // import messages
  import Fetcher._

  // set up Akka
  import akka.pattern.pipe
  import context.dispatcher
  implicit val materializer = ActorMaterializer()

  // set up HTTP
  val http = Http(context.system)
  val request = HttpRequest(uri = Config.rlecache.upstream.endpoint)

  // process messages
  def receive = {
    case Fetch =>
      log.info("Request data from {}.", request.uri)
      http.singleRequest(request).pipeTo(self)
    case HttpResponse(StatusCodes.OK, headers, entity, _) =>
      log.info("Request succeeded.")
      entity.dataBytes
        .via(Framing.delimiter(ByteString("\n"), Config.rlecache.upstream.maximumLineLength, true))
        .map(_.utf8String)
        .runWith(Sink.seq)
        .map(Cacher.Put)
        .pipeTo(cacher)
    case resp @ HttpResponse(code, _, _, _) =>
      log.error("Request failed, response code: {}", code)
      resp.discardEntityBytes()
  }
}
