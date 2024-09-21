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

object Kahan {
  // Perform Kahan summation, returning a Stream with a single element that is
  // the total
  def sum(stream: Stream[Pure, Float]): Stream[Pure, Float] =
    ???

  // Perform Kahan summation, returning a Stream where each element is the sum
  // of elements in the input up to that point.
  //
  // This is often more useful in a streaming application because the input may
  // never end, or we may want the most up-to-date result at any given point in
  // time.
  def cumulativeSum(stream: Stream[Pure, Float]): Stream[Pure, Float] =
    ???
}
