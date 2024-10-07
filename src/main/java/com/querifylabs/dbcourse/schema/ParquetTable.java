package com.querifylabs.dbcourse.schema;

import com.querifylabs.dbcourse.NotImplementedException;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.TranslatableTable;
import org.apache.calcite.schema.impl.AbstractTable;

public class ParquetTable extends AbstractTable implements TranslatableTable {
    public String schema() {
        throw new NotImplementedException();
    }

    public String name() {
        throw new NotImplementedException();
    }

    @Override
    public RelNode toRel(RelOptTable.ToRelContext toRelContext, RelOptTable relOptTable) {
        throw new NotImplementedException();
    }

    @Override
    public RelDataType getRowType(RelDataTypeFactory relDataTypeFactory) {
        throw new NotImplementedException();
    }
}
