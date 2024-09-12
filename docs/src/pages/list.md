# Stream as a List

`Stream` is the main type in FS2. We're going to develop several mental models to understand how `Stream` works. We'll start with a really simple model: `Stream` is just a fancy `List`.


## Working With Stream

We can construct a `Stream` just like we'd construct a `List`.

```scala mdoc
import fs2.*

val s = Stream(1, 2, 3, 4, 5)
```

As you can see from what is printed, it is indeed a fancy `List`.
We have some odd type parameter, and it's so fancy that it doesn't print its value.

We can see it does contain the values we expect by converting it to a `List`.

```scala mdoc
s.toList
```

We can do the majority of the things we'd do with a `List` with `Stream`.
Here's another example.

```scala mdoc
s.map(x => x + 1).toList
```

You don't need me to reiterate the `List` API here, as I'm sure you're familiar with it.
It's time for you to write some code. 
Go and do the code exercise in `code/src/main/scala/introduction/01-list.scala`.

@:solution
Here's one way you could solve the problem. For several of the questions you could use different methods to achieve the same effect.

```scala mdoc:silent
object StreamAsList {
  // Construction. We focus on creating Streams

  // The empty stream produces no values
  val empty: Stream[Pure, Int] = 
    Stream.empty

  // This Stream should produce 1, 2, and 3, in that order
  val naturals: Stream[Pure, Int] = 
    Stream(1, 2, 3)

  // This method accepts a single value and returns the Stream that produces
  // that single value
  def one[A](value: A): Stream[Pure, A] = 
    Stream.emit(value)

  // This method accepts a list of values and returns the Stream that produces
  // exactly those values in the order given.
  def list[A](values: List[A]): Stream[Pure, A] = 
    Stream.emits(values)

  // Transformation. We focus on transforming existing Streams

  // This method returns a stream where every element in `stream` is incremented
  // by `value`.
  def add(stream: Stream[Pure, Int], value: Int): Stream[Pure, Int] =
    stream.map(x => x + value)

  // This methods return a stream that only contains the values of the input
  // stream that match the predicate.
  def only[A](
      stream: Stream[Pure, A],
      predicate: A => Boolean
  ): Stream[Pure, A] = 
    stream.filter(predicate)

  // This method should sum all the values in the given `stream` and return a
  // Stream containing just a single value, the total.
  def sum(stream: Stream[Pure, Int]): Stream[Pure, Int] =
    stream.fold(0)((accum, elt) => accum + elt)
}
```
@:@


## Streaming Algorithms: Kahan Summation

We haven't yet seen what differentiates a `Stream` from a `List`, but we've seen enough to look at our first streaming algorithm.
This algorithm, known as Kahan summation, performs the apparently simple job of summing numbers.

Floating point numbers are kinda goofy. One issue is that they have finite precision. This can lead to surprising results from simple arithmetic. Let's see an example with `Float`, instead of `Double`, as it's easier to see the problem with lower precision numbers.

Here's one billion written as a `Float`. (Did you know you can use the `_` separator in Scala to write numbers? I didn't until recently. The `f` suffix makes the literal a `Float` instead of `Double`.)

```scala mdoc
val billion = 1_000_000_000.0f
```

Let's add 40,000 to it.

```scala mdoc
billion + 40_000f
```

Easy enough. Let's do the same in a roundabout way.

```scala mdoc
(billion :: List.fill(10_000)(4.0f)).foldLeft(0.0f)(_ + _)
```

Hmmm. We are out by 40,000. This occurs because a `Float` can only store between 6 and 9 decimal digits of precision. As a result, one billion (represented as a `Float`) plus one rounds to one billion.

```scala mdoc
1_000_000_000f + 1f
```

There are three possible solutions: 

1. we can use a higher precision numeric type;
2. we can cry, because life is unfair;
3. we can use a clever algorithm like [Kahan summation][kahan]. 

For this exercise we'll choose option 3.

The [Wikipedia explanation][kahan] is clear enough that I'm not going to repeat a description here. Implement Kahan summation in `code/src/main/scala/introduction/02-kahan.scala`.

@:solution
There are two parts to this. The first, harder, part is to implement Kahan summation itself. The second part is to remove the state that Kahan summation needs from the output of the `Stream`. Here's how I did it.

```scala mdoc:silent
object Kahan {
  final case class KahanSum(total: Float, carry: Float) {
    def +(input: Float): KahanSum = {
      val y = input - carry
      val nextTotal = total + y
      val nextCarry = (nextTotal - total) - y

      KahanSum(nextTotal, nextCarry)
    }
  }
  object KahanSum {
    val empty = KahanSum(0.0f, 0.0f)
  }

  // Perform Kahan summation, returning a Stream with a single element that is
  // the total
  def sum(stream: Stream[Pure, Float]): Stream[Pure, Float] =
    stream.fold(KahanSum.empty)((accum, elt) => accum + elt).map(_.total)

  // Perform Kahan summation, returning a Stream where each element is the sum
  // of elements in the input up to that point.
  //
  // This is often more useful in a streaming application because the input may
  // never end, or we may want the most up-to-date result at any given point in
  // time.
  def cumulativeSum(stream: Stream[Pure, Float]): Stream[Pure, Float] =
    stream.scan(KahanSum.empty)((accum, elt) => accum + elt).map(_.total).tail
}
```
@:@

[kahan]: https://en.wikipedia.org/wiki/Kahan_summation_algorithm
