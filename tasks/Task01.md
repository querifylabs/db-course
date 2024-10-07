# Introduce parquet schema reader to Optimizer infrastructure
In this task, you will get familiar with the Apache Calcite basic components 
and will implement custom classes `ParquetSchema` and `ParquetTable` to list
schemas and tables.

`ParquetSchema` will be initialized with a path to a folder containing schemas.
The folder is assumed to have the following structure:
```
- schema_1
  - table_1
    - <file1>.parquet
    - <file2>.parquet
    ...
  - table_2
    ...
- schema_2
    ...
```
Assume that all parquet files within one table directory have the same schema, so
you can pick an arbitrary file for table creation.

## Steps to implementation
  * Implement listing directories to recreate schema and table hierarchy
  * Use `ParquetFileReader` (dependency is already included) to read parquet metadata 
    and convert parquet schema to table row type
  * Update `CustomDataTypeSystem` to properly handle timestamp types
  * In `toRel()` method of table implementation, return an instance of `LogicalTableScan`

## Additional Exercises
  * Study how different components of the optimizer are assembled together in `CourseOptimizer`
  * Check the difference between the parsed AST tree and relational operator tree
  * Play with case sensitivity settings. What changes are required to properly support
    case-insensitive or case-sensitive catalog?
