package rlecache

import org.scalatest._

class RLESpec extends FlatSpec with Matchers {
  "Compressed.apply" should "represent runs of length 1 as Single objects" in {
    Compressed(1, "foo") should be (Single("foo"))
  }

  it should "represent runs of length > 1 as Repeat objects" in {
    Compressed(2, "foo") should be (Repeat(2, "foo"))
    Compressed(42, "answer") should be (Repeat(42, "answer"))
  }

  "Compressed.unapply" should "extract runs of length 1 from Single objects" in {
    Single("foo") match {
      case Compressed(count, element) =>
        count should be (1)
        element should be ("foo")
    }
  }

  it should "extract runs from Repeat objects" in {
    Repeat(1, "foo") match {
      case Compressed(count, element) =>
        count should be (1)
        element should be ("foo")
    }
    Repeat(2, "foo") match {
      case Compressed(count, element) =>
        count should be (2)
        element should be ("foo")
    }
    Repeat(42, "answer") match {
      case Compressed(count, element) =>
        count should be (42)
        element should be ("answer")
    }
  }

  import Compressor._

  "Compressor.compress" should "compress the empty sequence to itself" in {
    compress(Seq()) should be (Seq())
  }

  it should "compress sequences with distinct elements to sequences of Single objects" in {
    compress(Seq("hello")) should be (Seq(Single("hello")))
    compress(Seq("world")) should be (Seq(Single("world")))
    compress(Seq("hello", "world")) should be (Seq(Single("hello"), Single("world")))
  }

  it should "compress sequences of identical elements to a single run" in {
    compress(Seq("foo")) should have length (1)
    compress(Seq("foo", "foo")) should have length (1)
    compress(Seq("foo", "foo", "foo")) should have length (1)
  }

  it should "compress arbitrary sequences correctly" in {
    compress(Seq("foo", "foo", "bar", "bar", "bar", "foo")) should be
      (Seq(Repeat(2, "foo"), Repeat(3, "bar"), Single("foo")))
  }

  "Compressor.decompress" should "decompress the empty sequence to itself" in {
    decompress(Seq()) should be (Seq())
  }

  it should "decompress Single objects to runs of length 1" in {
    decompress(Seq(Single("foo"))) should be (Seq("foo"))
    decompress(Seq(Single("hello"), Single("world"))) should be (Seq("hello", "world"))
  }

  it should "decompress Repeat objects to runs of the correct length" in {
    decompress(Seq(Repeat(1, "foo"))) should have length (1)
    decompress(Seq(Repeat(2, "foo"))) should have length (2)
    decompress(Seq(Repeat(42, "foo"))) should have length (42)
  }

  it should "decompress arbitrary sequences correctly" in {
    decompress(Seq(Repeat(2, "foo"), Repeat(3, "bar"), Single("foo"))) should be
       (Seq("foo", "foo", "bar", "bar", "bar", "foo"))
  }
}
