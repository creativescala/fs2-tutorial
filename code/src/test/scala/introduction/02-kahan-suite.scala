package introduction

import fs2.*
import munit.FunSuite

class KahanSuite extends FunSuite {
  val billion = 1_000_000_000.0f
  val numbers = billion :: List.fill(10_000)(4f)

  test("sum") {
    val total = Kahan.sum(Stream.emits(numbers)).toList

    assertEquals(total, List(1_000_040_000f))
  }

  test("cumulativeSum returns cumulative sums") {
    val totals = Kahan.cumulativeSum(Stream(1f, 2f, 3f, 4f, 5f)).toList
    assertEquals(totals, List(1f, 3f, 6f, 10f, 15f))
  }

  test("cumulativeSum correctly computes sum") {
    val totals = Kahan.cumulativeSum(Stream.emits(numbers)).last.toList
    assertEquals(totals, List(Some(1_000_040_000f)))
  }
}
