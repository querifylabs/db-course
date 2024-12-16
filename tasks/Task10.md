# Implement hash aggregate
In this task, we will implement a basic non-vectorized hash aggregate. The implementation
steps will be pretty much in line with the previous exercises and will conclude a basic
query engine allowing basic aggregates execution.

## Steps to implementation
 * Create an `AggregationNode` class and plug it to the `PlanImplementor`
 * `AggregateNode` should work in two modes: a global mode (for aggregates without grouping keys)
and a regular mode (with aggregates with grouping keys)
 * For regular mode the aggregate node may use a regular hash map and use a generic
key that is constructed from the page based on column indices
 * As a value, hash map may use an `Accumulator` class that accepts an input value and may produce
a result
  * Since the `AggregateNode` is a blocking node, it should consume all the input from the child
and produce the result afterward
  * To run the tests you will also need to implement a project node with trivial projects (no complex
expression calculation support is required)

# Additional exercises
 * Investigate how a vectorized version of an aggregate will work
 * What would it take to process queries with aggregates when the aggregate result
does not fit RAM?
