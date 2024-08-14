# FS2 Tutorial

This is a self-guided tutorial to [FS2][fs2], using streaming data analytics for the majority of examples.
It's a combination of text and coding exercises.


## Plan

This is the rough plan for contents:

- FS2 `Stream` as akin to `List`
  - The `Stream` API works like `List`, except:
    - contents are (potentially) arranged in time not space
    - separation between description and action (as in `IO`) so need to "`compile`" the `Stream`
  - Basic examples: map, filter, etc.
  - Calculating averages
    - Naive approach
    - Incremental approach (e.g. https://math.stackexchange.com/questions/106700/incremental-averaging)
    - EWMA
- Effects in FS2
- Fan-in and fan-out
  - Pull based model
  - Tools for each
- Streaming algorithms
  - Single pass
  - Streaming average
  - Reservoir sampling
  - Hyperloglog
  - Sliding and tumbling windows

[fs2]: https://fs2.io/
