package com.querifylabs.dbcourse;

import org.apache.calcite.rel.type.RelDataTypeSystemImpl;
import org.apache.calcite.sql.type.SqlTypeName;

public class CustomDataTypeSystem extends RelDataTypeSystemImpl {
    @Override
    public int getMaxPrecision(SqlTypeName typeName) {
        switch (typeName) {
            case TIMESTAMP, TIMESTAMP_TZ:
                return 6;
            default:
                return super.getMaxPrecision(typeName);
        }
    }
}
