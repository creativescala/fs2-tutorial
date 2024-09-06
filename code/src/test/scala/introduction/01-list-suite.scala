package introduction

import fs2.*
import munit.FunSuite

class StreamAsListSuite extends FunSuite {
  test("empty") {
    assertEquals(StreamAsList.empty.toList, List.empty)
  }

  test("naturals") {
    assertEquals(StreamAsList.naturals.toList, List(1, 2, 3))
  }

  test("one") {
    assertEquals(StreamAsList.one(1).toList, List(1))
    assertEquals(StreamAsList.one("a").toList, List("a"))
  }

  test("list") {
    assertEquals(StreamAsList.list(List(1, 2, 3)).toList, List(1, 2, 3))
    assertEquals(StreamAsList.list(List("a", "b", "c")).toList, List("a", "b", "c"))
  }
}
