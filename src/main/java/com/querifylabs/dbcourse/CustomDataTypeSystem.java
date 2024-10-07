package com.querifylabs.dbcourse;

import org.apache.calcite.rel.type.RelDataTypeSystemImpl;
import org.apache.calcite.sql.type.SqlTypeName;

public class CustomDataTypeSystem extends RelDataTypeSystemImpl {
    @Override
    public int getMaxPrecision(SqlTypeName typeName) {
        switch (typeName) {
            case TIMESTAMP:
            case TIMESTAMP_TZ:
                return 9;
            default:
                return super.getMaxPrecision(typeName);
        }
    }
}
