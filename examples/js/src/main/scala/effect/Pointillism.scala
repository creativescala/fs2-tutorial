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
import cats.syntax.all.*
import doodle.core.*
import doodle.svg.*
import doodle.svg.effect.Frame
import doodle.syntax.all.*

import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.annotation.JSExportTopLevel
import scala.util.Random

@JSExportTopLevel("EffectPointillism")
object Pointillism {
  @JSExport
  def draw(id: String): Unit = {
    val frame =
      Frame(id)
        .withSize(600, 600)
        .withBackground(Color.midnightBlue)

    val randomSize: IO[Double] =
      IO(Random.nextInt(7)).map(_ + 5).map(_.toDouble)

    val randomAlpha: IO[Normalized] =
      IO(Random.nextDouble() * 0.5 + 0.5).map(_.normalized)

    val randomColor: IO[Color] =
      randomAlpha.map(alpha => Color.hotpink.alpha(alpha))

    def point(location: Point): IO[Picture[Unit]] =
      (randomSize, randomColor).mapN { (size, color) =>
        Picture.circle(size).fillColor(color).noStroke.at(location)
      }

    frame
      .canvas()
      .use { canvas =>
        val clicks = canvas.mouseClick

        clicks
          .evalMap(pt => point(pt))
          .scan(Picture.empty)((pts, pt) => pt.on(pts))
          .evalMap(picture => canvas.render(picture))
          .compile
          .drain
      }
      .unsafeRunAsync(_ => ())
  }
}
