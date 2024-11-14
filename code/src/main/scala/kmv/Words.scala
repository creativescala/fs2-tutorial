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

import cats.effect.IO
import fs2.Stream
import fs2.io.file.*

import java.net.URI
import java.nio.file.Paths
import scala.util.hashing.MurmurHash3

object Words {
  val resource: URI = getClass().getResource("words.txt").toURI()
  val nioPath = Paths.get(resource)
  val words = Path.fromNioPath(nioPath)

  val stream: Stream[IO, String] = Files.forIO.readUtf8Lines(words)

  def hash(in: String): Int =
    MurmurHash3.bytesHash(in.getBytes())

  val maxUnsignedInteger = 0x0000_0000_ffff_ffffL.toDouble

  def intToNormalizedDouble(in: Int): Double =
    Integer.toUnsignedLong(in).toDouble / maxUnsignedInteger
}
