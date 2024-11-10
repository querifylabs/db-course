# Convert Logical to Physical nodes
The key concept of the cost-based optimization in Apache Calcite is conversion to physical nodes.
The logical nodes that we used so far have infinite cost by default and cannot be used in cost-based
optimization. While there are workarounds for it, the canonical way to use cost-based optimizations
in Calcite is converting the logical nodes to the custom physical nodes. Custom physical nodes can
have tweaked cost model and embed additional information that is not available in standard nodes.

We have prepared necessary infrastructure and set up the custom convention that defines the physical
nodes, so you need to only implement the physical nodes themselves and rules that convert from logical
nodes to physical nodes.

## Steps to implementation
* Create classes for physical scan, project, and aggregate operators similar to `PhysicalFilter`
* Create rules for scan, project, and aggregate conversion similar to `PhysicalFilter` rule. Note that
if a node has a child, the rule **must** convert the child to the physical convention as well when constructing
the physical counterpart. See how this is done in the filter rule
* Add all created rules to the `ToPhysicalRules.ALL` collection

## Additional questions and exercises
* When conversion to physical plan is finished, use `VolcanoPlanner.toDot()` method to dump the internal
structure of the planner memo in dot format and use any graphvis visualizer (e.g. edotor.net) to visualize
the memo. Study how `RelSet` and `RelSubset` are organized and their relationship
* Add an instance of `RelOptListener` to volcano planner to capture the state of the planner before and after
rule invocation. You can capture the states and see the memo changes step-by-step as planner progresses with
the rules.
* Make sure that `ParquetTable.toRel()` passes `toRelContext.getCluster().traitSet()` as a trait set to the created
`LogicalTableScan`, otherwise you will get an assertion error when converting logical plan to physical plan
