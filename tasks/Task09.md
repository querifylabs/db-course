# Implement basic splits pruning
In this task, we will exploit pushed down filters to skip reading of whole files during the scan.
To implement this, you will need to add a custom infrastructure to collect and store file column
statistics and use this statistics during table scan. This task is intended to give you maximum
flexibility with the implementation.

In the task, we will collect statistics for `tpep_pickup_datetime` and `tpep_dropoff_datetime`
columns. The idea behind that is an assumption that the column values are correlated with the
file structure, and we will be able to skip whole files when the filter range is narrow enough. 

## Steps to implementation
 * Choose a format to store statistics. This may be a json file or any other format of your choice.
 * Build a procedure to extract and store statistics from the parquet files for selected columns. 
You can either scan the file values similarly to how the scan operator is implemented or use a more 
low-level parquet reader to extract data pages statistics if they exist. You can choose to have a 
 simple executable to collect and save statistics beforehand or collect statistics in a catalog 
 during start. For the test only min/max values will suffice
 * Provide a way to associate file statistics with the file names during the scan
 * In the table scan node, when a filter is pushed down to the table scan, use collected statistics
to check whether the filter drops the file according to statistics
 * Depending on how you implemented the scan node, add a test that validates that data skipping
is working correctly

# Additional exercises
 * What changes to the interface are required for a clean test implementation?
 * What other statistics can be collected and for what kind of filters?
