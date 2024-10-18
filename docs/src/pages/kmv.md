# k-Minimum Values

In this section we're dive into a relatively involved case study using FS2 in a typical application of data streaming: counting unique items. This allows us to explore practical usage of FS2 as well as learn about what you'll hopefully find a very interesting algorithm: **k-Minimum Values** (kMV).

Our task is to estimate the number of **distinct values** in a data stream. That is, the number of values remaining if we remove all values that have already occurred. If we want to use fancy words, we can say we're estimating **cardinality**, which is the name of "the number of elements in a set". This kind of problem arises very often in data analysis. Examples of distinct values problems in practice include calculating the number of unique visitors to a web site, or the number unique clients connecting to a network.

Distinct value problems are easy to solve when the amount of data is small. In Scala we could just put all the data into a `Set` data structure and call the `size` method. However, there are situations where there is too much data, or it arrives too quickly, for this to work. Using a `Set` requires we store all the distinct values, which means the amount of memory used grows in direct proportion to number of distinct values. In the terminology of computational complexity we'd say memory usage has order \\(O(n)\\), where \\(n\\) is the number of elements we observe. Similarly the time taken to insert an item into a `Set` is in the worst case proportional to \\(log n\\), so for \\(n\\) elements the total time is of order \\(O(n log n)\\).

We can solve both our memory and time problems by using k-Minimum Values.
k-Minimum Values is both a **streaming algorithm** and a **sketch**.
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

Your first mission is to implement the k-Minimum Values algorithm.
At this point we're not worrying about connecting it to FS2.


## Hashing Data


## Pipes


## Reading and Processing Text


## References

http://www.vldb.org/pvldb/vol11/p499-harmouch.pdf



[murmur3]: https://www.scala-lang.org/api/current/scala/util/hashing/MurmurHash3$.html#
