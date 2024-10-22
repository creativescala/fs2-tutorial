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

  /** Add the given element to this KMinimumValues sketch.
    *
    * In implementing this method you can choose to imperatively update internal
    * state, which might give you a more efficient implementation, or a pure
    * implementation that does not mutate state and is easier to reason about.
    */
  def add(element: Double): KMinimumValues = ???

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
  def distinctValues: Long = ???
}
