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
}
