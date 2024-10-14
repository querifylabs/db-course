# Use visitors pattern to implement constant folding for custom function
One of the important optimizations that optimizers do is constant folding: for
expressions that consist only of constant literals, optimizer can deduce the 
result of the expression before the plan is executed.

For example, in a query `select * from table where a = 1 + 2` the expression `1 + 2` 
can be folded to a constant `3`. Apache Calcite has a built-in functionality to fold
built-in supported functions, however, this executor is not aware of the custom function
that we introduced in the Task 2.

In this exercise, we will introduce a custom Rel and Rex visitor that will detect calls
to `base64decode` with string literal as an argument and replace it with a decoded literal.
We will assume that if the argument is not a valid base64-encoded string, the function 
returns `NULL`.

## Steps to implementation
* Override necessary `visit(...)` calls in `FoldTreeRelVisitor`. Notice how the default 
  implementation `RelShuttleImpl` works to ensure proper replacement order: we want bottom-most
  operators to be replaced first (i.e. bottom-up tree traversal).
* Override `visitCall(...)` in `FoldConstantsRelVisitor` to detect `base64decode` function calls
  and replace them with corresponding literals
* Use `java.util.Base64` class for decoding
* Use `RelNode.getCluster().getRexBuilder()` to get an instance of `RexBuilder` class to create
  literals and use `RexBuilder.makeBinaryLiteral()` to create decoding result.

## Additional Exercises
* The implemented constant folding method is quite simplistic. Can you think of other optimization
  opportunities that may become available after the constant folding was executed?
