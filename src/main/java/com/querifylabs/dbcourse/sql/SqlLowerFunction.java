package com.querifylabs.dbcourse.sql;

import org.apache.calcite.sql.SqlFunction;
import org.apache.calcite.sql.SqlFunctionCategory;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.type.SqlOperandTypeChecker;
import org.apache.calcite.sql.type.SqlOperandTypeInference;
import org.apache.calcite.sql.type.SqlReturnTypeInference;
import org.checkerframework.checker.nullness.qual.Nullable;

// TODO: Task 2 extend SqlFunction to use in ExtendedSqlOperatorTable
public class SqlLowerFunction extends SqlFunction {

    public SqlLowerFunction(String name, SqlKind kind, @Nullable SqlReturnTypeInference returnTypeInference, @Nullable SqlOperandTypeInference operandTypeInference, @Nullable SqlOperandTypeChecker operandTypeChecker, SqlFunctionCategory category) {
        super(name, kind, returnTypeInference, operandTypeInference, operandTypeChecker, category);
    }
}
