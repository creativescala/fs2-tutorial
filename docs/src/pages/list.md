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

[kahan]: https://en.wikipedia.org/wiki/Kahan_summation_algorithm
