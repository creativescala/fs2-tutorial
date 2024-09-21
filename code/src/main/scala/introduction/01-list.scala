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

// These exercises are all about the similarities between List and Stream.
// You're asked to create a Stream that produces certain values. There are tests
// check that the Stream you create is equivalent to a particular List. Run the
// tests to check your work, or look at the tests if you don't understand what a
// task is asking you to do.
object StreamAsList {
  // Construction. We focus on creating Streams

  // The empty stream produces no values
  val empty: Stream[Pure, Int] = Stream(0)

  // This Stream should produce 1, 2, and 3, in that order
  val naturals: Stream[Pure, Int] = Stream(0)

  // This method accepts a single value and returns the Stream that produces
  // that single value
  def one[A](value: A): Stream[Pure, A] = ???

  // This method accepts a list of values and returns the Stream that produces
  // exactly those values in the order given.
  def list[A](values: List[A]): Stream[Pure, A] = ???

  // Transformation. We focus on transforming existing Streams

  // This method returns a stream where every element in `stream` is incremented
  // by `value`.
  def add(stream: Stream[Pure, Int], value: Int): Stream[Pure, Int] = ???

  // This methods return a stream that only contains the values of the input
  // stream that match the predicate.
  def only[A](
      stream: Stream[Pure, A],
      predicate: A => Boolean
  ): Stream[Pure, A] = ???

  // This method should sum all the values in the given `stream` and return a
  // Stream containing just a single value, the total.
  def sum(stream: Stream[Pure, Int]): Stream[Pure, Int] = ???
}
