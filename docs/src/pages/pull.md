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
