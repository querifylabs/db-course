# Add custom function definition
Apache Calcite supports a lot of predefined functions from SQL standard, but sometimes
it may be required to add a number of custom functions with some tailored functionality.

In this task, you will add a custom function `base64decode` that will be recognized by
the validator.

The newly added `base64decode` function will accept a (var)character argument and return
varbinary type.

NOTE: This task only adds a definition of the function to the optimizer, but does not
provide any implementation.

## Steps to implementation
  * Implement `SqlBase64Decode` class by extending `SqlFunction`. It is only necessary to
   call a proper constructor.
  * Add an instance of the function to `ExtendedSqlOperatorTable`.

## Additional Exercises
  * What happens when you call `base64decode(<integer_column>)`? Can you provide a way
    to enforce only character arguments and raise an error otherwise?
