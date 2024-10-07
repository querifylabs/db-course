package com.querifylabs.dbcourse.schema;

import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.impl.AbstractSchema;

import java.util.Map;

// TODO Task 1: Implement reading parquet schema from a folder.
public class ParquetSchema extends AbstractSchema {
    public ParquetSchema(String rootPath, RelDataTypeFactory relDataTypeFactory) {
    }

    @Override
    protected Map<String, Schema> getSubSchemaMap() {
        return super.getSubSchemaMap();
    }
}
