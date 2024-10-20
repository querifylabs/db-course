package com.querifylabs.dbcourse.sql;

import org.apache.calcite.sql.SqlFunction;
import org.apache.calcite.sql.SqlFunctionCategory;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.type.OperandTypes;
import org.apache.calcite.sql.type.ReturnTypes;
import org.apache.calcite.sql.util.ReflectiveSqlOperatorTable;

public class ExtendedSqlOperatorTable extends ReflectiveSqlOperatorTable {
    private static ExtendedSqlOperatorTable instance;

    public static synchronized ExtendedSqlOperatorTable instance() {
        if (instance == null) {
            instance = new ExtendedSqlOperatorTable();
            instance.init();

            instance.register(lower());
            instance.register(upper());
        }

        return instance;
    }

    private static SqlFunction lower() {
        return new SqlLowerFunction("lower", SqlKind.OTHER_FUNCTION, ReturnTypes.VARCHAR, null, OperandTypes.STRING, SqlFunctionCategory.USER_DEFINED_FUNCTION);
    }

    private static SqlFunction upper() {
        return new SqlUpperFunction("upper", SqlKind.OTHER_FUNCTION, ReturnTypes.VARCHAR, null, OperandTypes.STRING, SqlFunctionCategory.USER_DEFINED_FUNCTION);
    }
}
