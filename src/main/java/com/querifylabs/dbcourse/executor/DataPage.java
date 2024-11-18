package com.querifylabs.dbcourse.executor;

import org.apache.arrow.vector.ValueVector;

import java.util.List;

public class DataPage implements AutoCloseable {
    private final List<? extends ValueVector> columns;

    public DataPage(List<? extends ValueVector> columns) {
        this.columns = columns;
    }

    public List<? extends ValueVector> getColumns() {
        return columns;
    }

    @Override
    public void close() {
        for (var column : columns) {
            column.close();
        }
    }
}