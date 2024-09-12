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
