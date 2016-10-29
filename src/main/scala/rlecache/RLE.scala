// RLE cache microservice
// Copyright 2016 Tillmann Rendel.
// All Rights Reserved.

package rlecache

import scala.annotation.tailrec

trait Compressor {
  def compress[A]: Seq[A] => Seq[Compressed[A]]
  def decompress[A]: Seq[Compressed[A]] => Seq[A]
}

sealed trait Compressed[+A]
case class Single[A](element: A) extends Compressed[A]
case class Repeat[A](count: Int, element: A) extends Compressed[A]

/** Abstract over Single vs. Repeat. */
object Compressed {
  def apply[A](count: Int, element: A) =
    if (count == 1)
      Single(element)
    else
      Repeat(count, element)

  def unapply[A](compressed: Compressed[A]): Option[(Int, A)] =
    compressed match {
      case Single(element) => Some(1, element)
      case Repeat(count, element) => Some(count, element)
    }
}

/** Concrete RLE compression implementation */
object Compressor extends Compressor {
  /** Compress sequence. */
  def compress[A] = {
    case Seq() => Seq()
    case head +: tail =>
      val builder = Seq.newBuilder[Compressed[A]]

      @tailrec
      def collect(count: Int, element: A, seq: Seq[A]) {
        seq match {
          case Seq() =>
            builder += Compressed(count, element)
          case head +: tail if head == element =>
            collect(count + 1, element, tail)
          case head +: tail =>
            builder += Compressed(count, element)
            collect(1, head, tail)
        }
      }
      collect(1, head, tail)

      builder.result()
  }

  /** Decompress sequence. */
  def decompress[A] =
    for {
      Compressed(count, element) <- _
      _ <- 1 to count
    } yield (element)

  /** Index into an RLE-encoded sequence. */
  @tailrec
  final def index[A](seq: Seq[Compressed[A]], idx: Int): A =
    seq match {
      case _ if idx < 0 => throw new IndexOutOfBoundsException
      case Seq() => throw new IndexOutOfBoundsException
      case Compressed(count, element) +: rest if idx < count => element
      case Compressed(count, element) +: rest => index(rest, idx - count)
    }

  /** Return decompressed length of RLE-encoded sequence. */
  def decompressedLength : Seq[Compressed[Any]] => Int =
    _.map {
      case Compressed(count, _) => count
    }.sum
}
