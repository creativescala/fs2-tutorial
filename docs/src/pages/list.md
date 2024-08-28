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
Complete the challenge in code/src/main/scala/introduction/01-list.scala.
**Insert exercise here**
