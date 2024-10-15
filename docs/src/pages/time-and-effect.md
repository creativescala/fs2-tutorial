# Effects over Time

In the [previous section](list.md) we approached `Stream` as a fancy `List`. 
In this section we'll develop a more useful model for understanding `Stream`: as effects over time. 
Let's look at this in two parts, considering first values over time and then effects.

We highly recommend the [Aquascape][aquascape] site as a companion for this section.
It has graphical descriptions of the methods we'll encounter.


## Values in Time and Space

We can think of a `List` as values arranged in *space*; in particular in the computer's memory.
Each value inside a `List` lives somewhere in the computer's memory, and they are there for all time. (Ok, they are not actually there forever but from the point the `List` is created until it is garbage collected. However we cannot attempt to observe a `List` before or after it is created ---Scala is *memory safe*---so from the point of view of our program the values are always there whenever we go looking for them.)

A `Stream`, however, can represent values arranged in *time*. At any point in time a `Stream` may produce additional values. This can model, for example, data arriving from the network or disk, or user input. The corollary of data arranged over time is that at some point in time there may be no data. This could be because no data has yet arrived, or because we've processed all the data that has arrived and we're waiting for more (which may or may not arrive).

See the [Aquascape section for time][aquascape-time] for a detailed description of available methods that deal with time.

@:callout(info)

#### Type Inference and Temporal Instances

The methods that manipulate time have an `implicit` parameter / `using` clause that looks for an instance of a type class `Temporal`. This will often fail to find an instance if you call these methods on a `Pure` `Stream`. When this happens you see an error message like

```
No given instance of type cats.effect.kernel.Temporal[[x] =>> fs2.Pure[x]] 
was found for a context parameter of method metered in class Stream.
... lots more stuff here ...
```
The solution is to simply specify the type parameter to give type inference the help it needs. That is, instead of writing, say,

```scala
Stream(1, 2, 3).metered(1.second)
```

you should write

```scala mdoc:silent
import fs2.*
import cats.effect.*
import scala.concurrent.duration.*

Stream(1, 2, 3).metered[IO](1.second)
```
@:@

## Effects

A `Stream` can also represent *effects* that occur to produce the values that make up the `Stream`. Disk, network, and user input all are effects, and `Stream` allows us to model them as such.

An important implication of this is that there is a *separation between description and action*. A `Stream` is a description of some effects that will produce values over time. Only when we put those effects into action do we actually produce the values. 

If you've worked through the [Cats Effect tutorial][cats-effect-tutorial], or are otherwise familiar with effects, this part should be familiar.

Now we understand this we can better understand the type signature of `Stream`. So far we've seen types like

```scala
Stream[Pure, Float]
```

This tells us that the `Stream` has no effects (this is what `Pure` means) and produces values of type `Float`.

More generally, the type is

```scala
Stream[F, A]
```

which is a `Stream` of effects of type `F` that produce values of type `A`.


## Running Streams

In our previous examples we have converted `Stream` into `List`. This only works because our `Streams` are `Pure`. If they have some other effect type the method is not callable.

```scala
val effect: Stream[IO, Int] = Stream(1, 2, 3)
effect.toList
// -- [E008] Not Found Error: -----------------------------------------------------
// 1 |effect.toList
//   |^^^^^^^^^^^^^
//   |value toList is not a member of fs2.Stream[cats.effect.IO, Int], 
//.  |but could be made available as an extension method.
//.  |
```

To run an effectful `Stream` we must first convert it into some runnable effect type, and when we do so we must specify what we want to do with all the values in the `Stream`. Here's what is possibly the simplest example.

```scala mdoc:invisible
val effect: Stream[IO, Int] = Stream(1, 2, 3)
```
```scala
import cats.effect.unsafe.implicits.global

effect.compile.drain
```

Let's walk through this.

1. Calling `compile` says we want to convert this `Stream` into an effect, specifically the `F` type in the `Stream`. In this case that is `IO`.

2. Calling `compile` returns an object with methods that specify how to handle any values the `Stream` produces. We've called `drain`, which means "keep running the `Stream` until it has no more values, but throw away the values it produces". Calling `drain` indicates we're running the `Stream` purely for it's effects, which is a common occurrence when a `Stream's` job is to read values from somewhere (an effect) and then write them somewhere else (also an effect).

3. Our result is an `IO`, which is a value we can run by calling, for example, `unsafeRunSync.`


@:exercise(Guess the Effect)
What do you think will happen when you run `effect` above? Make sure you come up with a guess before running the code to see what actually happens.
@:@

@:solution
The result is a great big nothing burger.

```scala
effect.compile.drain.unsafeRunSync()
```

This is because the underlying `Stream` doesn't have any effects that actually produce any output. It's actually a pure `Stream`, but we're allowed to pretend that such a `Stream` has effects of any other type.
@:@


## Effects in Streams

We've seen how to run `Streams`, but how do we actually put effects into a `Stream`?

@:exercise(Run the Effects)
Look through the `Stream` API and find the methods that:

1. allow us to construct a `Stream` that runs an effect to produce a value; and
2. allow us to effectfully transform a value.

Give an example of each.
@:@

@:solution
The methods are:

1. `Stream.eval` to create a `Stream` that runs an effect to produce a value; and
2. `evalMap` to effectfully transform a value.

You may have found other methods, particularly for the second part.

Here's an example using the former:

```scala
Stream.eval(IO.println("Hello streams!")).compile.drain.unsafeRunSync()
// Hello streams!
```

And an example of the latter:

```scala
Stream(1, 2, 3).evalMap(a => IO.println(a).as(a)).compile.drain.unsafeRunSync()
// 1
// 2
// 3
```
@:@


@:exercise(Time for Time)
Create a `Stream` that emits a value once every second. You can emit any values you like (a few numbers is a good choice).
@:@

@:solution
In the solution below I use `metered` to emit a value every second. Of the available combinators I think this is the best choice, but you could reasonably use other combinators, such as `spaced`, for this task. The semantics are slightly different, but the description of the task is not precise enough to require a particular choice of combinator. 

I used `evalMap` to add the effect of printing the values so that I can see they are indeed emitted over time.

```scala mdoc:silent
Stream(1, 2, 3).metered[IO](1.second).evalMap(IO.println)
```
@:@

[cats-effect-tutorial]: https://creativescala.org/cats-effect-tutorial
[aquascape]: https://zainab-ali.github.io/aquascape/
[aquascape-time]: https://zainab-ali.github.io/aquascape/reference/time.html
