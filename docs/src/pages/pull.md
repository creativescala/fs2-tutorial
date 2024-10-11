# Stream as Stages and Pulls

In this section we'll learn our final model for FS2 streams: as consisting of stages and pulls. 
This is the "real" model of FS2.

Let's start with a simple example.

```scala mdoc:silent
import cats.effect.IO
import fs2.Stream
import cats.effect.unsafe.implicits.global

val data = Stream(1, 2, 3, 4)
val a = data.evalMap(a => IO.println(s"a: $a"))
val b = data.evalMap(b => IO.println(s"b: $b"))
```

What do you think happens when run the following? 
Take a guess before trying out the code.

```scala mdoc:compile-only
b.compile.drain.unsafeRunSync()
```

@:solution
You should see output like the below.

```
b: 1
b: 2
b: 3
b: 4
```

Notice that the `a` branch does not run. Did you expect this behaviour?
@:@

The simple reason that we see output from `b` but not from `a` is that we didn't run `a`. 
However, to understand why `a` does not run introduces some of the core concepts in FS2.
Remember that a `Stream` is a description.
This is, it's a data structure that describes what we want to happen.
We build a `Stream` from upstream (which produces data) to downstream (which consumes data.)
The downstream data structures have references to those upstream of them.
When we write

```scala
val data = Stream(1, 2, 3, 4)
val a = data.evalMap(a => IO.println(s"a: $a"))
val b = data.evalMap(b => IO.println(s"b: $b"))
```

we create three data structures:

1. `data`, which is the source of data;
2. `a`, which is downstream of `data` and has a reference to `data`; and
3. `b`, which is also downstream of `data` and has a reference to `data`.

Notice that `b` does not have any reference to `a`. 
So when we run `b` we *cannot* run `a`, because we have no reference to it.

We'll call each data structure a **stage**.
A stage `x` is downstream of another stage `y` if it consumes data directly from `y`, or it consumes data from a stage that is downstream of `y`.
Likewise we can say that `y` is upstream of `x` if `x` is downstream of `y`.

When we run a stage, we only run it and all stages that are upstream from it.
In the example above, `a` is not upstream of `b` so it does not run when `b` runs.
