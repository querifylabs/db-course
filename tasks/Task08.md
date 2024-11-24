# Implement basic expression evaluation
In this task, we will add support for some of the basic expressions. In real projects, expression evaluation contributes
a very large subset of the codebase, so we will focus only on a tiny fraction of the supported expressions to demonstrate
the basic approach. You can add support for more expressions if you like as an additional exercise.

For this task we will focus on the filter operation and ability to compare dates with a user-defined literal, so we will
implement support for `<=`, `>=` expressions on timestamp type, as well as `AND` operator to combine them
during filtering. So far we will craft the queries such that we avoid project nodes.

## Steps to implementation
* Create a new `FilterNode` that will hold the filter to be evaluated
* Create expression nodes for the operators we will use. You will need:
  * A node that extracts the corresponding column from the input page (this node will handle `RexInputRef`)
  * A node that produces a vector based on given literal (this node will handle `RexLiteral`)
  * Node(s) that will handle comparison(s) (to handle `RexCall` with comparison operators and timestamp operand types)
  * Node that will handle `AND` logical operator (to handle `RexCall` with `AND` operator)
  * For example, the `AND` node `evaluate()` method may look as follows:
    ```
    var leftData = leftInput.evaluate(page);
    var rightData = rightInput.evaluate(page);
    // Both leftData and rightData must be BitVectors
    // combine them using AND and return
    // finally close leftData and rightData if they were allocated by inputs (i.e. not owned by the page)
    ```
* Create a visitor that will traverse the `RexNode` tree and create a tree of `ExpressionNode` that can be evaluated 
* Filter node should evaluate the predicate against the input page and then filter the page according to the produced
bit vector
* Prefer vectorized execution in the expression evaluation, i.e. you likely will have a distinct tight loops over the
vector elements to evaluate a concrete operator
* Apache Arrow requires to `close` the vectors once they are no longer used, so you need to `close` them inside
the filter operator implementation as well as in expression nodes implementation. Note that a page column can be 
referenced more than once by in input ref, and the page columns will be closed when the page is closed. You can use
`Page#ownsVector` to avoid closing page vectors

# Additional exercises
* Try benchmarking the sample filter query after JVM warmup. What are the noticeable time consumers?
* Try switching from vectorized execution to per-row operator dispatch and check performance difference
* What other optimizations can be done so far based on the current observations? Will they change any of the interfaces?
* Implement more expressions of your choice (e.g. strict comparisons, equals, not equals)
