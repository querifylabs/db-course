# Implement scan operator with projection pushdown
In this task, we will start moving towards the query execution. We will start by implementing a scan node which will
execute the part related to `PhysicalTableScan`. For now, we will omit the part related to the filter that is pushed
to the scan and focus only on the projected columns (note that we pushed filters only as a hint, so the constructed
scan will remain correct)

In the execution framework we will use Apache Arrow utility library to represent the data in the columnar format. Follow
arrow [documentation](https://arrow.apache.org/docs/java/) to learn how to create value vectors. In the task, you can
use an instance of `ExecutionContext` to get an instance of arrow memory allocator.

## Steps to implementation
* Create a descendant of `BaseExecutionNode` that you will use for implementing a parquet scan
* Inside `PlanImplementor`, use a visitor pattern again to traverse the plan tree and create the converted nodes. In 
this task, the visitor will only support `PhysicalTableScan` nodes
* Update `ParquetTable` to have a method that returns a list of files to be scanned
* In the scan node implementation, use arrow-dataset library to read data from parquet files. Note that we need to 
construct a projected schema and use it to create a new scanner to read only the specified columns.
