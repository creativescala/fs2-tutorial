/*
 * Copyright 2024 Creative Scala
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
