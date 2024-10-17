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
import kmv.KMV.arithmeticMean

import java.util.Arrays
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.annotation.JSExportTopLevel
import scala.util.Random

final class KMV(_elements: Array[Double]) {
  def add(element: Double): KMV = {
    val idx = Arrays.binarySearch(_elements, element)
    // element is larger than all the values in _elements
    if idx >= _elements.size then this
    // element is already in _elements
    else if _elements(idx) == element then this
    else {
      // Shift all the larger _elements out of the way and insert element
      System.arraycopy(_elements, idx, _elements, idx + 1, _elements.size - idx)
      _elements(idx) = element
      this
    }
  }

  def elements: IArray[Double] =
    IArray.from(_elements)

  def averageDistance: Double = {
    KMV.harmonicMean(_elements)
  }

  def cardinality: Long =
    KMV.estimateCardinality(averageDistance)
}
object KMV {
  def arithmeticMean(elements: Array[Double]): Double = {
    def loop(idx: Int, sum: Double): Double =
      if idx == 0 then loop(idx + 1, elements(0))
      else if idx == elements.size then (sum / elements.size)
      else loop(idx + 1, sum + (elements(idx) - elements(idx - 1)))

    loop(0, 0.0)
  }

  def harmonicMean(elements: Array[Double]): Double = {
    def loop(idx: Int, sum: Double): Double =
      if idx == 0 then loop(idx + 1, 1.0 / elements(0))
      else if idx == elements.size then (elements.size / sum)
      else loop(idx + 1, sum + (1.0 / (elements(idx) - elements(idx - 1))))

    loop(0, 0.0)
  }

  def estimateCardinality(mean: Double): Long = {
    val estimatedCardinality = Math.round((1.0 / mean) - 1)

    estimatedCardinality
  }
}

@JSExportTopLevel("KMinimumValues")
object KMinimumValues {
  @JSExport
  def numberLine(id: String): Unit = {
    val frame =
      Frame(id)
        .withSize(600, 300)
        .withBackground(Color.white)

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

    def nPoints = 16
    def points: Seq[Double] =
      List.fill(nPoints)(Random.nextDouble())

    val kmv = new KMV(points.toArray)
    val averageDistance = KMV.arithmeticMean(kmv.elements.toArray)
    val cardinality = kmv.cardinality
    val cardinalityArithmeticMean =
      KMV.estimateCardinality(arithmeticMean(kmv.elements.toArray))

    points
      .map(point)
      .allOn
      .on(line)
      .margin(0, 0, 20, 0)
      .above(
        Picture
          .text(s"Average distance between regions: $averageDistance")
          .margin(0, 0, 10, 0)
          .above(
            Picture.text(
              s"Estimated set cardinality (harmonic mean): $cardinality"
            )
          )
          .margin(0, 0, 10, 0)
          .above(
            Picture
              .text(
                s"Estimated set cardinality (arithmetic mean): $cardinalityArithmeticMean"
              )
          )
          .fillColor(Color.black)
      )
      .drawWithFrame(frame)
  }
}
