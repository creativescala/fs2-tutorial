# k-Minimum Values

In this section we are diving into a relatively involved case study using FS2 in a typical application of data streaming: counting unique items. This allows us to explore practical usage of FS2 as well as learn about what you'll hopefully find a very interesting algorithm: *k-Minimum Values* (kMV).

Our task is to estimate the number of *distinct values* in a data stream. That is, the number of values remaining if we remove all values that have already occurred. If we want to use fancy words, we can say we're estimating *cardinality*, which is the name of "the number of elements in a set". This kind of problem arises very often in data analysis. Examples of distinct values problems in practice include calculating the number of unique visitors to a web site, or the number unique clients connecting to a network.

Distinct value problems are easy to solve when the amount of data is small. In Scala we could just put all the data into a `Set` data structure and call the `size` method. However, there are situations where there is too much data, or it arrives too quickly, for this to work. Using a `Set` requires we store all the distinct values, which means the amount of memory used grows in direct proportion to number of distinct values. In the terminology of computational complexity we'd say memory usage has order \\(O(n)\\), where \\(n\\) is the number of elements we observe. Similarly the time taken to insert an item into a `Set` is in the worst case proportional to \\(\log n\\), so for \\(n\\) elements the total time is of order \\(O(n \log n)\\).

We can solve both our memory and time problems by using k-Minimum Values.
k-Minimum Values is both a *streaming algorithm* and a *sketch*.
A stream algorithm is one that requires only a single pass over the data, never needing to return data it has already seen.
A sketch means we'll represent the data with some summary that takes dramatically less memory to store than the data itself.
In the case of k-Minimum Values the amount of memory required is constant, with order \\(O(k)\\).
These features come with a tradeoff: we will not get a exact answer, but only one that is close to correct with high probability.


## A Sketch of k-Minimum Values

Let's start with an intuitive explanation of k-Minimum Values. Detailed mathematical analysis can be found in the references.

Our task is to estimate the number of distinct elements in a data set. 
Imagine that each element is a random number, uniformly distributed in the range \\(\[0, 1\]\\). 
Just one distinct element splits the range into two regions. 
Two elements splits it into three regions, three elements into four regions, and so on. 

Let's now think about the average length of the regions. 
With two regions (when there is one element) the average length is \\(\frac{1}{2}\\), as the sum of the lengths must be \\(1\\).
With three regions it must be \\(\frac{1}{3}\\), with four regions \\(\frac{1}{4}\\), and so on.
What this tells us is that the average length of the regions tells us how many distinct values there are, which is what we are trying to learn.
Specifically, with \\(n\\) elements the average length of the regions is \\(\frac{1}{n + 1}\\).

If we keep around *all* the elements we see we can get an exact measurement of the number of the elements from the average length of the regions.
This is, however, a bit pointless. 
Not only could we just count the number of elements directly, but what we're trying to do is explicitly *not* keep them all around.
The leap that k-Minimum Values takes is to realize that the average length of a subset of the regions is a noisy estimate of the average length of all the regions, so we only need to keep around, say, \\(k\\) elements. 
Which elements should we choose?
We need to keep around a contiguous block, so that we correctly estimate the average length.
The smallest \\(k\\) values will do.
And there we have it: k-Minimum Values.

So, in summary, k-Minimum Values works as follows:

1. We observe a stream of data uniformly distributed in the range \\(\[0, 1\]\\).
2. We keep the \\(k\\) smallest values we observe.
3. We can estimate the total number of distinct element from the average length of the \\(k\\) regions between \\(0\\) and the largest value we keep.

Does it actually work? 
Below is an example where we draw 32 random values and keep only 16 of them (i.e. \\(k\\) is 16.)
From the 16 samples we keep we estimate the total size. 
You can reload the page a few times to see how the estimate changes.

@:doodle("number-line32", "KMinimumValues.numberLine32")

For contrast, here's another example with \\(k\\) set to 16, and a total of 16384 values.
As you can see all the minimum values are squooshed down at the left-hand side of the number line, and the estimation is still reasonably accurate..

@:doodle("number-line16384", "KMinimumValues.numberLine16384")

There is only one problem: we have assumed that elements are uniformly distributed in \\(\[0, 1\]\\). 
We won't be so lucky with real data.
However there is a trick we can use: we can hash the data.
A good hash function will give us a result that is uniformly distributed in the range of its output, which is typically 32-bits to 512-bits.
For our purposes 32-bits will do, which will give us an integer we can convert to a fractional `Double` without loss of precision.
In Scala we can use [scala.util.hashing.MurmurHash3][murmur3].


## Implementing k-Minimum Values

We're going to implement a distinct values estimate system using k-Minimum Values.
This means implementing the core algorithm as well as the support code around it that feeds it data. 
This latter part is where FS2 will come in.


### The Algorithm

Your first mission is to implement the k-Minimum Values algorithm.
There is a code skeleton in `code/src/main/scala/kmv/KMinimumValues.scala`.
There are also simple tests in `code/src/test/scala/kmv/KMinimumValuesSuite.scala`.

@:solution
My solution uses a mutable array. I felt this like challenging myself to muck around with array indices and other concepts that I don't use much in my day-to-day programming. A solution using an immutable data structure would be a lot simpler to write.

```scala
final class KMinimumValues(k: Int) {
  // The k minimum values, stored in a mutable array
  private val values = Array.ofDim[Double](k)

  // Values will be initialized to contain all zeros, which will be less than
  // most reasonable input. Hence we need to track how many elements in values
  // have been initialized with real data.
  private var used = 0

  def add(element: Double): KMinimumValues = {
    import java.util.Arrays

    // A +ve index indicates the element is in the array.
    //
    // A -ve index indicates the element is not in the array, and gives the
    // insertion point - 1 for the element.
    //
    // Only search in the elements of values that have been used
    val idx = Arrays.binarySearch(values, 0, used, element)

    // Element is already in the array
    if idx >= 0 then this
    else {
      if used < values.size then used = used + 1

      val insertionPoint = -idx - 1
      // Element is larger than any existing value
      if insertionPoint >= values.size then this
      else {
        // Shift all the larger values out of the way and insert element
        System.arraycopy(
          values,
          insertionPoint,
          values,
          insertionPoint + 1,
          values.size - insertionPoint - 1
        )
        values(insertionPoint) = element
        this
      }
    }
  }

  def distinctValues: Long =
    // If we have seen fewer than k values we can return the exact number of
    // distinct values
    if used < values.size then used.toLong
    else Math.round(k.toDouble / values.last - 1.0)
}
```
@:@


### Building a Data Pipeline

We're now going to build the pipeline that will feed the k-Minimum Values algorithm.
This will have the following stages:

- reading text from storage;
- segmenting the text into words; and
- hashing the words into `Double` values between 0 and 1.

For all of these parts we will use FS2.

For data we will use two sources:

1. A list of English words. This file has one word per line, and every word is unique, so it gives us an easy way to test our algorithm.

2. The complete works of William Shakespeare. This is much bigger than the dictionary, contains duplicates, and requires more processing, and so is a more realistic test.

We will start with the word list and, once we have k-Minimum Values working, move on to Billy S.

Neither of our test cases are big enough that we really need to use k-Minimum Values; we could use a traditional algorithm instead. 
This is intentional. 
It is useful to be able to compare to a known correct result,
and working with truly big data requires file sizes and processing times that are onerous.


#### Reading and Processing Text

There are several freely available word lists. [This word list][wordlist.10000] has a mere 10,000 words, while [this one][wordlist] has about 270,000 words, and [this one][english-words] has over 460,000. In this case bigger is better, so go [grab the big one][english-words] unless your computer is struggling.

If you downloaded the biggest list you'll have a file named `words.txt` containing 466549 lines. Copy it into the directory `code/src/main/resources`. 

@:callout(info)
#### Resources and the JVM

Resources are a moderately obscure, but very useful, feature of the JVM.
Code often depends on some data. 
In this case we depend on the word list.
A web site might depend on some icons, and many programs depend on configuration.

It is useful to be able to bundle this data with our code, so our code can always find it in the same place. 
Resources allows us to do this. 
Any file in `/src/main/resources` is a resource that is included with our code, and can be accessed in the same way no matter where we run our code from or where it is copied to.
@:@


We have to jump through a few hoops to load a resource into a FS2 `Stream`.
First we construct a URI referring to the resource.

```scala
val resource = getClass().getResource("words.txt").toURI()
```

Then we construct a Java `Path` referring to the resource.

```scala
import java.nio.file.Paths

val nioPath = Paths.get(resource)
```

Then we construct a FS2 `Path`, which is part of a useful library FS2 provides for dealing with files and paths.

```scala
import fs2.io.file.*

val words = Path.fromNioPath(nioPath)
```

Finally we create a `Stream`, which gives us each line as a separate item in the `Stream`.

```scala
val stream: Stream[IO, String] = Files.forIO.readUtf8Lines(words)
```

So quite a few lines of code, but it's not hard code to understand.


#### Hashing Data

The next stage is to hash each word. This will give us an `Int` which is (approximately) uniformly distributed in across the range. We can then convert this to a `Double` in the range 0 to 1, which is what k-Minimum Values requires.

This might be a fun bit of code to write if you've done lower level programming before, so I'm not going to give you all the code right away. Instead I'm going to go through the pieces needed to construct the working code. Check the solution if you get stuck.

The first piece we need is our hash function. In Scala we will use `scala.util.hashing.MurmurHash3`. We can convert a `String` to an `Array[Bytes]` using the `getBytes` method, and then hash those bytes to an `Int`.

Once we have an `Int` we need to convert it to a `Double` in the range \\(\[0, 1\]\\) without losing information. This is a little bit trickier than it seems as an `Int` can have negative values. We can convert the `Int` to a `Long`, treating the `Int` as an unsigned value, covert that `Long` to a `Double`, and then divide by the maximum unsigned `Int`. This is where the small amount of bit-twiddling comes in. I'll give you one hint: you probably want to use `Integer.toUnsignedLong`.

There is a code skeleton in `code/src/main/scala/kmv/Words.scala`, which also includes the code to load the word list from the resources (assuming you named the file `"words.txt"`.)

@:solution

Here's the complete code to read and hash the word list.

```scala mdoc:silent
import cats.effect.IO
import fs2.Stream
import fs2.io.file.*

import java.net.URI
import java.nio.file.Paths
import scala.util.hashing.MurmurHash3

object Words {
  val resource: URI = getClass().getResource("words.txt").toURI()
  val nioPath = Paths.get(resource)
  val words = Path.fromNioPath(nioPath)

  val stream: Stream[IO, String] = Files.forIO.readUtf8Lines(words)

  def hash(in: String): Int =
    MurmurHash3.bytesHash(in.getBytes())

  val maxUnsignedInteger = 0x0000_0000_ffff_ffffL.toDouble

  def intToNormalizedDouble(in: Int): Double =
    Integer.toUnsignedLong(in).toDouble / maxUnsignedInteger
}
```
@:@


#### Pipes



## References

http://www.vldb.org/pvldb/vol11/p499-harmouch.pdf



[murmur3]: https://www.scala-lang.org/api/current/scala/util/hashing/MurmurHash3$.html#

[wordlist.10000]: https://www.mit.edu/~ecprice/wordlist.10000
[wordlist]: https://proofingtoolgui.org/
[english-words]: https://github.com/dwyl/english-words

[hashing]: https://github.com/mpilquist/blog-hashing/blob/main/README.md
