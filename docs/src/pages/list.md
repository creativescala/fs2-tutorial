# Stream as a List

`Stream` is the main type in FS2. We're going to develop several mental models to understand how `Stream` works. We'll start with a really simple model: `Stream` is just a fancy `List`.


## Constructing Stream

We can construct a `Stream` just like we'd construct a `List`.

```scala mdoc
import fs2.*

Stream(1, 2, 3, 4, 5)
```
