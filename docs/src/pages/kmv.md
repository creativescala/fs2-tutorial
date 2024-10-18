# k-Minimum Values

In this section we're going to break from learning FS2 fundamentals to see a relatively involved example of an algorithm designed for streaming data. This will allow us to explore practical usage of FS2 as well as learn about what you'll hopefully find a very interesting algorithm: **k-Minimum Values** (kMV).

Our task is to estimate **set cardinality**, which is a fancy way of saying "the number of elements in a set". We're going to assume our input is actually a multi-set, meaning the same element can occur multiple times, and we want to know how many unique elements there are. (If we could guarantee we would never see any duplicates we could just count the elements directly.) This is a very typical problem in data analysis, where we want to know, for example, the number of unique visitors to a web site over a day or other time period.

We'll make our problem interesting by requiring we only visit each data point exactly once.
Hence the running time of the algorithm has order \\(O(n)\\).
This means we require a **streaming algorithm**.
This requirement could be because there is too much data to load into memory, 
or the data must be processed too quickly to store.
This means the straightforward solution, of just loading everything into a `Set` data structure, won't work, as this has running time of order \\(O(n log n)\\).

k-Minimum Values is a streaming algorithm, and it is also an example of a **data sketch**.
Not only is it's running time \\(O(n)\\), but the memory it requires is **sub-linear**.
In fact the memory requirement is fixed, with order \\(O(k)\\).
These features come with a tradeoff: we will not get a exact answer, but one that is close to correct with high probability.


## A Sketch of k-Minimum Values

Let's start with an intuitive explanation of k-Minimum Values. Detailed mathematical analysis can be found in the references.

Our task is to estimate the number of distinct elements in a multi-set. 
Imagine that each element is a random number, uniformly distributed in the range \\(\[0, 1\]\\). 
Just one distinct element splits the range into two regions. 
Two elements splits it into three regions, three elements into four regions, and so on. 

Let's now think about the average length of the regions. 
With two regions (when there is one element) the average length is \\(\frac{1}{2}\\), as the sum of the lengths must be \\(1\\).
With three regions it must be \\(\frac{1}{3}\\), with four regions \\(\frac{1}{4}\\), and so on.
What this tells us is that the average length of the regions tells us how many elements there are in the multi-set, which is what we are trying to learn.
Specifically, with \\(n\\) elements the average length of the regions is \\(\frac{1}{n + 1}\\).

If we keep around *all* the elements we see we can get an exact measurement of the number of the elements from the average length of the regions.
This is, however, a bit pointless. 
Not only could we just count the number of elements directly, but what we're trying to do is explicitly *not* keep around all the elements.
The leap that k-Minimum Values takes is to realize that the average length of a subset of the regions is a noisy estimate of the average length of all the regions, so we only need to keep around, say, \\(k\\) elements. 
Which elements should we choose?
We need to keep around a contiguous block, so that we correctly estimate the average length.
The smallest values will do.
And there we have it: k-Minimum Values.

So, in summary, k-Minimum Values works as follows:

1. We observe a stream of data uniformly distributed in the range \\(\[0, 1\]\\).
2. We keep the \\(k\\) smallest values we observe.
3. We can estimate the total number of distinct element from the average length of the \\(k\\) regions between \\(0\\) and the largest value we keep.

At this point you probably have one of three reactions:

1. what the heck, I am so confused!?
2. this is nonsense and will never work!
3. this is incredibly clever and elegant!

Either way, an example will probably help.

@:doodle("number-line", "KMinimumValues.numberLine")

There is only one problem: we have assumed that elements are uniformly distributed in \\(\[0, 1\]\\). 
We won't be so lucky with real data.
However there is a trick we can use: we can hash the data.
A good hash function will give us a result that is uniformly distributed in the range of its output, which is typically 32-bits to 512-bits.
For our purposes 32-bits will do, which will give us an integer we can convert to a fractional `Double` without loss of precision.



## References

http://www.vldb.org/pvldb/vol11/p499-harmouch.pdf
