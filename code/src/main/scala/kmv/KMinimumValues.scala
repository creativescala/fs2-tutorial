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

/** Implement the k-Minimum Values sketch. This skeleton leaves many design
  * decisions up to you.
  */
final class KMinimumValues(k: Int) {
  // The k minimum values, stored in a mutable array
  private val values = Array.ofDim[Double](k)

  // Values will be initialized to contain all zeros, which will be less than
  // most reasonable input. Hence we need to track how many elements in values
  // have been initialized with real data.
  private var used = 0

  /** Add the given element to this KMinimumValues sketch.
    *
    * In implementing this method you can choose to imperatively update internal
    * state, which might give you a more efficient implementation, or a pure
    * implementation that does not mutate state and is easier to reason about.
    */
  def add(element: Double): KMinimumValues = {
    import java.util.Arrays

    // A +ve index indicates the element is in the array.
    //
    // A -ve index indicates the element is not in the array, and gives the
    // insertion point - 1 for the element.
    //
    // Only search in the elements of values that have been used
    val idx = Arrays.binarySearch(values, 0, used, element)

    // Element is already in the array
    if idx >= 0 then this
    else {
      if used < values.size then used = used + 1

      val insertionPoint = -idx - 1
      // Element is larger than any existing value
      if insertionPoint >= values.size then this
      else {
        // Shift all the larger values out of the way and insert element
        System.arraycopy(
          values,
          insertionPoint,
          values,
          insertionPoint + 1,
          values.size - insertionPoint - 1
        )
        values(insertionPoint) = element
        this
      }
    }
  }

  /** Get the estimated distinct values from this KMinimumValues sketch.
    *
    * The text describes this estimate as using the average distance between
    * regions. An equivalent estimate can be made using the kth value, which is
    * k times the average distance from 0, and hence estimates k / (n + 1),
    * where n is the number of distinct values. If we call this values length,
    * we can estimate the distinct values from length as
    *
    * distinct values = (k / length) - 1
    *
    * This requires less computation.
    */
  def distinctValues: Long =
    // If we have seen fewer than k values we can return the exact number of
    // distinct values
    if used < values.size then used.toLong
    else Math.round(k.toDouble / values.last - 1.0)

}
