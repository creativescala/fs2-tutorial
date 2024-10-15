# k-Minimum Values

In this section we're going to break from learning FS2 fundamentals to see an relatively involved example. This will allow us to explore practical usage of FS2 as well as learn about what you'll hopefully find a very interesting algorithm: **k-Minimum Values** (kMV).

kMV is an example of what is known as a **data sketch**.
Streaming. Linear time. Constant memory.
Set cardinality. PAC.


## A Sketch of kMV

Let's start with an intuitive explanation of kMV. Detailed mathematical analysis can be found in the references.

Our task is to estimate the number of distinct elements in a multi-set. 
Imagine that each element is a random number, uniformly distributed in the range $\[0, 1\]$. 
If we have just one distinct element, this splits the range into two regions. 
Two elements splits it into three regions, three elements into four regions, and so on. 
Now for the insight that makes kMV work. 
As we've declared that the elements are uniformly distributed, as the number of elements increases the average of the distance from an element to the immediately preceding one (or $0$, if we have chosen the smallest element) approaches $\frac{1}{n + 1}$. 
(We have $n + 1$ in the denominator because $n$ elements splits the interval into $n + 1$ regions.)

So here is k-Minimum Values in a nutshell:

- the distance from 
- we can estimate the number of distinct elements in our multi-set by inverting the distance from zero to the smallest element (the minimum value) 
