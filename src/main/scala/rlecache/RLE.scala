// RLE cache microservice
// Copyright 2016 Tillmann Rendel.
// All Rights Reserved.

package rlecache

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
