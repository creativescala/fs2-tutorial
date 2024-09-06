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

  test("add") {
    assertEquals(StreamAsList.add(Stream(1, 2, 3), 4).toList, List(5, 6, 7))
    assertEquals(StreamAsList.add(Stream(3, 5, 7), 2).toList, List(5, 7, 9))
  }

  test("only") {
    assertEquals(StreamAsList.only(Stream(1, 2, 3, 4, 5, 6, 7, 8, 9), _ % 2 == 0).toList, List(2, 4, 6, 8))
    assertEquals(StreamAsList.only(Stream(1, 2, 3, 4, 5, 6, 7, 8, 9), _ < 5 == 0).toList, List(1, 2, 3, 4))
  }

  test("sum") {
    assertEquals(StreamAsList.sum(Stream(1)).toList, List(1))
    assertEquals(StreamAsList.sum(Stream(1, 2, 3)).toList, List(6))
    assertEquals(StreamAsList.sum(Stream(1, 2, 3, 4, 5)).toList, List(15))
  }
}
