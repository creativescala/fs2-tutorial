# FS2 Tutorial

## What

This is a self-guided tutorial to [FS2][fs2], using streaming data analytics for the majority of examples.
It's a combination of text and coding exercises.

We have two main goals with this tutorial: to learn how to use FS2 effectively, and to learn about streaming algorithms.
Streaming algorithms are algorithms that calculate a result with only a single pass over their data. They are useful when working with data that is too big to fit into memory, or when working with data that arrives over time (such as from a network connection). FS2 is a natural fit for such algorithms.

The number one non-goal of this tutorial is to write a lot of material that already exists elsewhere. We'll refer to the following two documents for a lot of details:

* [FS2 Guide](https://fs2.io/#/guide)
* [FS2 API](https://www.javadoc.io/doc/co.fs2/fs2-core_3/latest/index.html)

## How

You should clone [this repository][repository] to your computer. In code/src/main/scala/ there are various code challenges you will work with. The text will tell you when to tackle each challenge.

[fs2]: https://fs2.io/
[repository]: https://github.com/creativescala/fs2-tutorial
