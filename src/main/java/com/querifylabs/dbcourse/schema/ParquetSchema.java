package com.querifylabs.dbcourse.schema;

import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.Table;
import org.apache.calcite.schema.impl.AbstractSchema;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

// TODO Task 1: Implement reading parquet schema from a folder.
public class ParquetSchema extends AbstractSchema {
    private Map<String, Table> tables = new HashMap<>();
    private Map<String, Schema> schemas = new HashMap<>();

    public ParquetSchema(String rootPath, RelDataTypeFactory relDataTypeFactory) {
        try {
            schemas = Files.walk(Path.of(rootPath), 1)
                    .filter(curPath -> !curPath.toString().equals(rootPath))
                    .collect(Collectors.toMap(
                            path -> path.getFileName().toString(),
                            path -> new ParquetSchema(path, relDataTypeFactory))
                    );
        } catch (IOException exception) {
            System.err.println(exception.getMessage());
        }
    }

    @Override
    protected Map<String, Schema> getSubSchemaMap() {
        return schemas;
    }

    @Override
    protected Map<String, Table> getTableMap() {
        return tables;
    }

    private ParquetSchema(Path rootPath, RelDataTypeFactory relDataTypeFactory) {
        try {
            tables = Files.walk(rootPath, 1)
                        .filter(curPath -> !curPath.equals(rootPath))
                        .collect(Collectors.toMap(
                                path -> path.getFileName().toString(),
                                path -> new ParquetTable(path, relDataTypeFactory, rootPath.getFileName().toString()))
                        );
        } catch (IOException exception) {
            System.err.println(exception.getMessage());
        }
    }
}
