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
import cats.syntax.all.*
import doodle.core.*
import doodle.java2d.*
import doodle.java2d.effect.Frame
import doodle.syntax.all.*
import fs2.*

import scala.util.Random

object Pointillism extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    val frame =
      Frame.default
        .withSize(600, 600)
        .withBackground(Color.midnightBlue)

    val randomSize: IO[Double] =
      IO(Random.nextInt(7)).map(_ + 5).map(_.toDouble)

    val randomAlpha: IO[Normalized] =
      IO(Random.nextDouble() * 0.5 + 0.5).map(_.normalized)

    val randomColor: IO[Color] =
      randomAlpha.map(alpha => Color.hotpink.alpha(alpha))

    def point(location: Point): IO[Picture[Unit]] =
      // You might want to use randomSize, randomAlpha, and randomColor above to
      // construct more interesting Pictures.
      IO.pure(Picture.circle(5).fillColor(Color.hotpink).noStroke.at(location))

    frame
      .canvas()
      .use { canvas =>
        // A stream of mouse clicks
        val clicks: Stream[IO, Point] = canvas.mouseClick

        // Call canvas.render with a Picture[Unit] to render that Picture.
        IO.pure(ExitCode.Success)
      }
  }
}
