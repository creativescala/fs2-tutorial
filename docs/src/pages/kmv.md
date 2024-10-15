# k-Minimum Values

In this section we're going to break from learning FS2 fundamentals to see an relatively involved example. This will allow us to explore practical usage of FS2 as well as learn about what you'll hopefully find a very interesting algorithm: **k-Minimum Values** (kMV).

kMV is an example of what is known as a **data sketch**.
Streaming. Linear time. Constant memory.
Set cardinality. PAC.


## A Sketch of k-Minimum Values

Let's start with an intuitive explanation of kMV. Detailed mathematical analysis can be found in the references.

Our task is to estimate the number of distinct elements in a multi-set. 
Imagine that each element is a random number, uniformly distributed in the range $\[0, 1\]$. 
Just one distinct element splits the range into two regions. 
Two elements splits it into three regions, three elements into four regions, and so on. 

Let's now think about the distance between successive elements, and for the smallest element the distance between 0 and that element (which is just the value of the element.)
With just one element the average will be $\fra{1}{2}$, but clearly there will be a lot of variation.
With two elements the average distance will be $\frac{1}{3}$.
With three elements it is, on average, $\frac{1}{4}$, and so on.
This is the remarkable insight that k-Minimum Values is based on: the average distance between elements tells us something about how many distinct elements there are.
Specifically, the average distance converges to $\frac{1}{n + 1}$, where $n$ is the number of distinct elements, as $n$ increases.

There is only one problem: we have assumed that elements are uniformly distributed in $\[0, 1\]$. 
We won't be so lucky with real data.
However there is a trick we can use: we can hash the data.
A good hash function will give us a result that is uniformly distributed in the range of its output, which is typically 32-bits to 512-bits.
For our purposes 32-bits will do, which will give us an integer we can convert to a `Double` without loss of precision.

