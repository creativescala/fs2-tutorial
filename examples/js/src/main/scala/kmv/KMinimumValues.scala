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

import cats.effect.*
import cats.effect.unsafe.implicits.global
import doodle.core.*
import doodle.svg.*
import doodle.svg.effect.Frame
import doodle.syntax.all.*

import java.util.Arrays
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.annotation.JSExportTopLevel
import scala.util.Random

final class KMinimumValues(k: Int) {
  // The k minimum values, stored in a mutable array
  private val values = Array.ofDim[Double](k)

  // Values will be initialized to contain all zeros, which will be less than
  // most reasonable input. Hence we need to track how many elements in values
  // have been initialized with real data.
  private var used = 0

  def elements: IArray[Double] =
    IArray.unsafeFromArray(values)

  def add(element: Double): KMinimumValues = {
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

  def averageDistance: Double =
    values.last / k.toDouble

  def distinctValues: Long =
    // If we have seen fewer than k values we can return the exact number of
    // distinct values
    if used < values.size then used.toLong
    else Math.round(k.toDouble / values.last - 1.0)
}
@JSExportTopLevel("KMinimumValues")
object KMinimumValues {

  def fromRandomData(k: Int, n: Int): KMinimumValues = {
    val points: Seq[Double] =
      List.fill(n)(Random.nextDouble()).sorted.take(k)

    points.foldLeft(KMinimumValues(k))((kmv, elt) => kmv.add(elt))
  }
  val line: Picture[Unit] =
    OpenPath.empty
      .moveTo(-300, 0)
      .lineTo(300, 0)
      .path
      .strokeColor(Color.midnightBlue)
      .strokeWidth(3)

  def point(value: Double): Picture[Unit] =
    Picture
      .circle(11)
      .noStroke
      .fillColor(Color.hotpink)
      .at((600.0 * value) - 300.0, 0.0)

  def numberLine(k: Int, n: Int): Picture[Unit] = {
    val kmv = fromRandomData(k, n)
    val averageDistance = kmv.averageDistance
    val distinctValues = kmv.distinctValues

    kmv.elements
      .map(point)
      .toList
      .allOn
      .on(line)
      .margin(0, 0, 20, 0)
      .above(
        Picture
          .text(s"Average distance between regions: $averageDistance")
          .margin(0, 0, 10, 0)
          .above(
            Picture
              .text(
                s"Estimated distinct values: $distinctValues"
              )
          )
          .margin(0, 0, 10, 0)
          .above {
            Picture
              .text(
                s"Actual distinct values: $n"
              )
          }
          .fillColor(Color.black)
      )
  }

  @JSExport
  def numberLine32(id: String): Unit = {
    val frame =
      Frame(id)
        .withSize(620, 200)
        .withBackground(Color.white)

    numberLine(16, 32).drawWithFrame(frame)
  }

  @JSExport
  def numberLine16384(id: String): Unit = {
    val frame =
      Frame(id)
        .withSize(620, 200)
        .withBackground(Color.white)

    numberLine(16, 16384).drawWithFrame(frame)
  }
}
