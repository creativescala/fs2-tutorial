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

package kmv

import munit.FunSuite

// The k-Minimum Values algorithm is deterministic, so we can test it with
// carefully chosen test data.
class KMinimumValuesSuite extends FunSuite {
  test("kMV correctly estimates distinct values from a single point") {
    val kmv = KMinimumValues(1).add(0.5)

    assertEquals(kmv.distinctValues, 1L)
  }

  test("kMV correctly estimates distinct values from equally spaced points") {
    val kmv = KMinimumValues(4).add(0.2).add(0.4).add(0.6).add(0.8)

    assertEquals(kmv.distinctValues, 4L)
  }

  test("kMV keeps only the minimum values") {
    val kmv = KMinimumValues(4)
      .add(0.9)
      .add(0.9)
      .add(0.2)
      .add(0.4)
      .add(0.9)
      .add(0.6)
      .add(0.9)
      .add(0.8)

    assertEquals(kmv.distinctValues, 4L)
  }
}
