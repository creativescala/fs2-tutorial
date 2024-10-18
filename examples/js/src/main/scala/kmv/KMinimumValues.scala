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

final class KMV(values: Array[Double]) {
  def add(element: Double): KMV = {
    val idx = Arrays.binarySearch(values, element)
    // element is larger than all the values in values
    if idx >= values.size then this
    // element is already in values
    else if values(idx) == element then this
    else {
      // Shift all the larger values out of the way and insert element
      System.arraycopy(values, idx, values, idx + 1, values.size - idx)
      values(idx) = element
      this
    }
  }

  val k: Int = values.size

  def elements: IArray[Double] =
    IArray.from(values)

  def averageDistance: Double = {
    KMV.arithmeticMean(elements)
  }

  def cardinality: Long =
    KMV.estimateCardinality(averageDistance)
}
object KMV {
  def arithmeticMean(elements: IArray[Double]): Double = {
    def loop(idx: Int, sum: Double): Double =
      if idx == 0 then loop(idx + 1, elements(0))
      else if idx == elements.size then (sum / elements.size)
      else loop(idx + 1, sum + (elements(idx) - elements(idx - 1)))

    loop(0, 0.0)
  }

  def harmonicMean(elements: IArray[Double]): Double = {
    def loop(idx: Int, sum: Double): Double =
      if idx == 0 then loop(idx + 1, 1.0 / elements(0))
      else if idx == elements.size then (elements.size / sum)
      else loop(idx + 1, sum + (1.0 / (elements(idx) - elements(idx - 1))))

    loop(0, 0.0)
  }

  def estimateCardinality(mean: Double): Long = {
    val estimatedCardinality = Math.round(1.0 / mean) - 1

    estimatedCardinality
  }

  def fromRandomData(k: Int, n: Int): KMV = {
    val points: Seq[Double] =
      List.fill(n)(Random.nextDouble()).sorted.take(k)

    val kmv = new KMV(points.toArray)
    kmv
  }
}

@JSExportTopLevel("KMinimumValues")
object KMinimumValues {
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
    val kmv = KMV.fromRandomData(16, 32)
    val averageDistance = KMV.arithmeticMean(kmv.elements)
    val cardinality = kmv.cardinality

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
                s"Estimated distinct values: $cardinality"
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
        .withSize(600, 200)
        .withBackground(Color.white)

    numberLine(16, 32).drawWithFrame(frame)
  }

  @JSExport
  def numberLine16384(id: String): Unit = {
    val frame =
      Frame(id)
        .withSize(600, 200)
        .withBackground(Color.white)

    numberLine(16, 16384).drawWithFrame(frame)
  }
}
