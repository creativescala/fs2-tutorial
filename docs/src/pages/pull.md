# Stages and Pulls

In this section we'll learn a more detailed model for FS2 streams: as consisting of stages and pulls. 

## Stages

Take the following simple example of a stream.

```scala mdoc:silent
import cats.effect.IO
import fs2.Stream

val data = Stream(1, 2, 3, 4)
val a = data.evalMap(d => IO.println(s"a: $d"))
```

This stream consists of two *stages*: `data`, which produces values, and `a`, which uses those values. We say that `a` is *downstream* of `data` (and `data` is *upstream* of `a`) as data flows from `data` to `a`.

Remember that a `Stream` is a description.
Concretely, it's a data structure that describes what we want to happen.
We build a `Stream` from upstream (which produces data) to downstream (which consumes data.)
The downstream data structures have references to those upstream of them.

When we write

```scala
val data = Stream(1, 2, 3, 4)
val a = data.evalMap(d => IO.println(s"a: $d"))
val b = data.evalMap(d => IO.println(s"b: $d"))
```

we create three data structures:

1. `data`, which is the source of data;
2. `a`, which is downstream of `data` and has a reference to `data`; and
3. `b`, which is also downstream of `data` and has a reference to `data`.

Notice that `b` does not have any reference to `a`. It is neither upstream nor downstream from `a`.

We can state this relationship more abstractly: A stage `y` is downstream of another stage `x` if it consumes data directly from `x`, or it consumes data from a stage that is downstream of `x`.
Likewise we can say that `x` is upstream of `y` if `y` is downstream of `x`.

Finally, we will call the most upstream stage the *source* and the most downstream stage the *sink*. In the example above the source is `data` and the sink is `a` (or `b`).


## Pulls

Data flows from upstream to downstream, but it only flows in response to demand for data. This demand is called a *pull*, and it flows from downstream to upstream (i.e. in the opposite direction from data.) There are several important implications of this:

1. data will not flow if there is no demand; and hence
2. data does not start being processed when it arrives at the source of the stream but when it the sink pulls it; and hence
3. back-pressure, which is control of the production of data in response to speed of processing, is automatic, as the sink will not pull more data until it has finished processing it's current input.

Finally, unless otherwise noted in the description of a method, stages in FS2 do not cache data. Thus each pull causes a complete traversal of the stream, from source to sink. There is never partially processed data sitting in the stream (except in special cases).

We can observe pulls happening with the following `Stream`.

```scala mdoc:silent
import scala.util.Random
import scala.concurrent.duration.*

val source = Stream.repeatEval(IO.print("Source -> ") >> IO(Random.nextInt()))
val intermediary = source.evalMap(int => IO.print("Intermediary -> ").as(int))
val sink = intermediary.evalMap(int => IO.println("Sink") >> IO.sleep(1.second)).take(4)
```

Run it in the usual way:

```scala mdoc:compile-only
import cats.effect.unsafe.implicits.global

sink.compile.drain.unsafeRunSync()
```

Pay careful attention to the output when you run this. You should see `Source -> Intermediate -> Sink` being immediately printed, and then a pause before each subsequent iteration. If the `Source` produced data as soon as it was ready we would see the output arriving in a different order.
