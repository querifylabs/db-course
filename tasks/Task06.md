# Implement full-fledged rules to push projection and filter info to table scan
In this exercise you will implement a combination of rules to push project and filter information to the physical table scan.
The main goal of these rules is to provide the scan with necessary information about the current query to facilitate
data skipping.

For data skipping, we will be using the following information:
* The list of unique columns used in the query. It will be embedded as a set of column indices in the `PhysicalTableScan`
* A list of simple column comparisons that can be used to skip data blocks that do not satisfy the filter based on statistics.
Note that filter information is only a hint and we need to keep the original filter node to execute the final filtering.

Pay attention to the way Apache Calcite uses column indices to address input columns. When we drop some columns from the
underlying scan node, the column references in the top operator will change. For example, a plan
```
PhysicalProject[column6=[$6], column5=[$5]]
  PhysicalTableScan[table]
```
can be transformed to this plan:
```
PhysicalProject[column6=[$1], column5=[$0]]
  PhysicalTableScan[table, projected=[5, 6]]
```
You need to rewrite the expressions in the upper operator according to the index substitution using the visitor pattern.
Various implementations of the `org.apache.calcite.util.mapping.Mapping` interface (e.g. a `PartialMapping` [5->0, 6->1]
for the transformation above) will be handy.

Note that you need to keep the non-pushed projections in the upper operator, i.e. the plan
```
PhysicalProject[expr0=+[$5, $6]]
  PhysicalTableScan[table]
```
can be transformed to the plan
```
PhysicalProject[expr0=+[$0, $1]]
  PhysicalTableScan[table, projected=[5, 6]]
```

For filtering, we want to extract only the simple comparisons that can be used with statistics, i.e. the plan
```
PhysicalFilter[filter=AND(LIKE($0, '%data%'), >($1, 10))]
  PhysicalTableScan[table, projected=[5, 6]]
```
can be transformed to the plan
```
PhysicalFilter[filter=AND(LIKE($0, '%data%'), >($1, 10))]
  PhysicalTableScan[table, projected=[5, 6], filter=>($6, 10)]
```
Note that when pushing filter to the scan with projected columns, you need to do an inverse transformation on the filter
expression.
Utils like  `org.apache.calcite.plan.RelOptUtil.conjunctions`, `org.apache.calcite.plan.RelOptUtil.disjunctions`, 
`org.apache.calcite.rex.RexUtil.expandSearch` may be handy when working with search expressions.

You can choose filter representation of your choice when embedding filter information into the scan.

## Steps to implementation
* Make sure to run `mvn clean compile` to generate `@Value.Immutable` classes to get rid of unknown class warnings in IDE
* Add necessary fields to the `PhysicalTableScan` node that represent projected columns and filter
* In the rule `onMatch()` call extract necessary information from the top operator (filters for filter, used columns for projection)
* Construct necessary index mapping for expression rewrite depending on what is being pushed, rewrite the expressions with a visitor
* Construct new operators as necessary. If new produced operators are identical to the original tree, bail out and do not
produce new alternatives
* Add newly added rules to the planner rules in `CourseOptimizer` constructor
* Override `PhysicalTableScan.computeSelfCost()` to decrease scan cost when only a subset of columns is used or there is a filter
Otherwise, the planner may choose the original scan as a node with the same cost
* Override `PhysicalTableScan.explainTerms()` method and include projected columns and embedded filter to the node digest.
Without this, planner will consider scan with and without projected columns identical and will deduplicate them, which is
not correct
* Override `PhysicalTableScan.deriveRowType()` to compute a correct row type when projected columns are pushed
