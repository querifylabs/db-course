# Use built-in Calcite functionality to drop unused fields from query
As discussed in the lectures, one of the most important optimizations in queries is data skipping.
In this exercise we will use a built-in Apache Calcite functionality to create project node(s)
that will limit the query result to keep only the columns that are used in the query. The created
project node will later be used to propagate necessary information to the physical scan node.

## Steps to implementation
* Use `SqlToRelConverter.trimUnusedFields()` call to create `LogicalProject` that will be placed
on top of the `LogicalScan` and will keep only used fields.
