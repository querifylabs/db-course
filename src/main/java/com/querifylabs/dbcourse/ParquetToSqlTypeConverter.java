package com.querifylabs.dbcourse;

import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.parquet.schema.OriginalType;
import org.apache.parquet.schema.PrimitiveType;

public class ParquetToSqlTypeConverter {
    public static SqlTypeName convertParquetTypeToSqlType(PrimitiveType primitiveType) {
        OriginalType originalType = primitiveType.getOriginalType();
        switch (PrimitiveType.PrimitiveTypeName.valueOf(primitiveType.getPrimitiveTypeName().name())) {
            case BOOLEAN:
                return SqlTypeName.BOOLEAN;
            case INT32:
                return SqlTypeName.INTEGER;
            case INT64:
                if (originalType == OriginalType.TIMESTAMP_MICROS) {
                    return SqlTypeName.TIMESTAMP;}
                return SqlTypeName.BIGINT;
            case FLOAT:
                return SqlTypeName.FLOAT;
            case DOUBLE:
                return SqlTypeName.DOUBLE;
            case BINARY:
            case FIXED_LEN_BYTE_ARRAY:
                if (originalType == OriginalType.UTF8) {
                    return SqlTypeName.VARCHAR;
                }
                return SqlTypeName.VARBINARY;
            case INT96:
                return SqlTypeName.TIMESTAMP_TZ;
            default:
                throw new IllegalArgumentException("Unsupported Parquet type: " + primitiveType);
        }
    }
}
