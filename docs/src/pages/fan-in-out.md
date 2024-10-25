# Fan-in and Fan-out

In this section we'll talk about how to connect one upstream stage to several downstream stages, known as *fan-out*, and several upstream stages to one downstream stage, known as *fan-in*.

Let's start with a simple example of fan-out, where we connect one upstream stage to two downstream stages.

```scala mdoc:silent
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import fs2.Stream

val data = Stream(1, 2, 3, 4)
val a = data.evalMap(a => IO.println(s"a: $a"))
val b = data.evalMap(b => IO.println(s"b: $b"))
```

What do you think happens when the following is run? 
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
When we call `b.compile.drain.unsafeRunSync()` this creates demand on `b`, which in turns creates demand on stages upstream of `b`. 
As `a` is not upstream of `b` it has no demand and hence does not run.
There is a lesson from this: if you're not thinking about how you do fan-in and fan-out you're probably doing it wrong.

Let's start with fan-in.
FS2 provides several ways to express fan-in:

* We can use `zip` if we want to pair up elements from two upstream streams.
* We can use `merge` if we don't care about what order we get elements from the upstream streams, and both upstream streams have the same type.
* We can use `either` if we don't care about order (like `merge`) but the two upstream streams have different types.

Write a stream `sink` that uses one of the methods above to express fan-in of `a` and `b`.
What do you think you'll see when you run `sink`? Does the actual output match your expectations?

@:solution
In our example both `a` and `b` have the same type (`Unit`) and order doesn't seem important. So I chose `merge`.

```scala mdoc:silent
val sink = a.merge(b)
```

The output of

```scala mdoc:compile-only
sink.compile.drain.unsafeRunSync()
```
is

```
a: 1
b: 1
a: 2
b: 2
a: 3
b: 3
a: 4
b: 4
```

so we can see that both `a` and `b` receive all the values from `data`.
@:@


Now let's look at the example of fan-in below. 
It's a modification of our previous example where we have an effectful source, 
which generates random data.

```scala mdoc:reset:silent
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import fs2.Stream
import scala.util.Random

val source = Stream.eval(IO(Random.nextDouble())).repeat
val a = source.evalMap(a => IO.println(s"a: $a"))
val b = source.evalMap(b => IO.println(s"b: $b"))

val sink = a.merge(b)
```

What do you think you'll see when the following is run?
Will `a` and `b` both see the same values?
How many times will each run?

```scala mdoc:compile-only
// We use take(4) to avoid running forever
sink.take(4).compile.drain.unsafeRunSync()
```

@:solution

Here's some example output I saw when I ran the code.

```
b: 0.7958715143801504
a: 0.0859159273103528
b: 0.7907351218379188
a: 0.7568956320150807
b: 0.16689974459747392
a: 0.2762585354975654
```

Notice that `a` and `b` saw different data!
@:@
