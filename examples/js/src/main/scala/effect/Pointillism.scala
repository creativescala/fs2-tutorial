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

package effect

import cats.effect.*
import cats.effect.unsafe.implicits.global
import doodle.core.*
import doodle.svg.*
import doodle.svg.effect.Frame
import doodle.syntax.all.*

import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("EffectPointillism")
object Pointillism {
  @JSExport
  def draw(id: String): Unit = {
    val frame =
      Frame(id)
        .withSize(600, 600)
        .withBackground(Color.midnightBlue)

    def curve(points: Seq[Point]): Picture[Unit] = {
      OpenPath
        .interpolatingSpline(points)
        .path
        .strokeWidth(7.0)
        .strokeColor(Color.hotpink)
    }

    frame
      .canvas()
      .use { canvas =>
        val clicks = canvas.mouseClick

        clicks
          .fold(List.empty[Point])((pts, pt) => pt :: pts)
          .map(pts => curve(pts))
          .evalMap(picture => canvas.render(picture))
          .compile
          .drain
      }
      .unsafeRunAsync(_ => ())
    ()
  }
}
