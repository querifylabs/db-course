package com.querifylabs.dbcourse.schema;

import com.querifylabs.dbcourse.ParquetToSqlTypeConverter;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.logical.LogicalTableScan;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.TranslatableTable;
import org.apache.calcite.schema.impl.AbstractTable;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.hadoop.util.HadoopInputFile;
import org.apache.parquet.schema.PrimitiveType;
import org.apache.parquet.schema.Type;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ParquetTable extends AbstractTable implements TranslatableTable {
    private final String schema;
    private final String name;
    private final RelDataTypeFactory relDataTypeFactory;
    private List<Column> columns;

    public ParquetTable(Path rootPath, RelDataTypeFactory relDataTypeFactory, String schema) {
        this.name = rootPath.getFileName().toString();
        this.schema = schema;
        this.relDataTypeFactory = relDataTypeFactory;

        Configuration configuration = new Configuration();
        try {
            Path path = Files.walk(rootPath, 1)
                    .filter(curPath -> !curPath.equals(rootPath))
                    .limit(1)
                    .findFirst()
                    .orElseThrow();

            columns = ParquetFileReader.open(HadoopInputFile.fromPath(new org.apache.hadoop.fs.Path(rootPath.toString(), path.getFileName().toString()), configuration))
                            .getFileMetaData()
                            .getSchema()
                            .getColumns()
                            .stream()
                            .map(columnDescriptor -> {
                                PrimitiveType primitiveType = columnDescriptor.getPrimitiveType();
                                SqlTypeName sqlTypeName = ParquetToSqlTypeConverter.convertParquetTypeToSqlType(primitiveType);
                                return new Column(sqlTypeName, primitiveType.getName(), primitiveType.getRepetition().equals(Type.Repetition.OPTIONAL));
                            })
                            .toList();
        } catch (IOException exception) {
            System.err.println(exception.getMessage());
        }
    }

    public String schema() {
        return schema;
    }

    public String name() {
        return name;
    }

    @Override
    public RelNode toRel(RelOptTable.ToRelContext toRelContext, RelOptTable relOptTable) {
        return LogicalTableScan.create(toRelContext.getCluster(), relOptTable, toRelContext.getTableHints());
    }

    @Override
    public RelDataType getRowType(RelDataTypeFactory relDataTypeFactory) {
        var builder = relDataTypeFactory.builder();

        columns.forEach(column -> {
            if (column.sqlTypeName.equals(SqlTypeName.TIMESTAMP) || column.sqlTypeName.equals(SqlTypeName.TIMESTAMP_TZ)) {
                builder.add(column.name, column.sqlTypeName, relDataTypeFactory.getTypeSystem().getMaxPrecision(column.sqlTypeName)).nullable(true);
            } else {
                builder.add(column.name, column.sqlTypeName);
            }
            builder.nullable(column.nullable);
        });

        return builder.build();
    }

    private static class Column {
        public SqlTypeName sqlTypeName;
        public String name;
        public boolean nullable;

        public Column(SqlTypeName sqlTypeName, String name, boolean nullable) {
            this.name = name;
            this.sqlTypeName = sqlTypeName;
            this.nullable = nullable;
        }
    }
}
