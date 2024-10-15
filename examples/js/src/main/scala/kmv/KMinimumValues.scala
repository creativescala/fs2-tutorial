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

import java.util.Arrays

final class KMV(elements: Array[Double]) {
  def add(element: Double): KMV = {
    val idx = Arrays.binarySearch(elements, element)
    // element is larger than all the values in elements
    if idx >= elements.size then this
    // element is already in elements
    else if elements(idx) == element then this
    else {
      // Shift all the larger elements out of the way and insert element
      System.arraycopy(elements, idx, elements, idx + 1, elements.size - idx)
      elements(idx) = element
      this
    }
  }

  def cardinality: Long = {
    def loop(idx: Int, sum: Double): Double =
      if idx == 0 then loop(idx + 1, elements(0))
      else if idx == elements.size then (sum / elements.size)
      else loop(idx + 1, sum + (elements(idx) - elements(idx - 1)))

    val averageDistance = loop(0, 0.0)
    val estimatedCardinality = Math.round((1.0 / averageDistance) - 1)

    estimatedCardinality
  }
}

object KMinimumValues {}
