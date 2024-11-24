package com.querifylabs.dbcourse.executor;

import org.apache.arrow.vector.BitVector;
import org.apache.arrow.vector.ValueVector;

import java.util.ArrayList;
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

    public DataPage select(ExecutionContext ctx, BitVector selection) throws ExecutorException {
        if (getRowCount() != selection.getValueCount()) {
            throw new ExecutorException("Invalid selection vector size");
        }
        var filtered = new ArrayList<ValueVector>(columns.size());
        for (var column : columns) {
            filtered.add(filterColumn(ctx, column, selection));
        }
        return new DataPage(filtered);
    }

    private ValueVector filterColumn(ExecutionContext ctx, ValueVector column, BitVector selection) {
        var transferPair = column.getTransferPair(ctx.getAllocator());
        int idx = 0;
        for (int r = 0; r < selection.getValueCount(); r++) {
            if (!selection.isNull(r) && selection.get(r) != 0) {
                transferPair.copyValueSafe(r, idx++);
            }
        }

        transferPair.getTo().setValueCount(idx);
        return transferPair.getTo();
    }

    public boolean isEmpty() {
        return getRowCount() == 0;
    }

    public int getRowCount() {
        return columns.getFirst().getValueCount();
    }

    public boolean ownsVector(ValueVector vector) {
        for (ValueVector col : columns) {
            if (vector == col) {
                return true;
            }
        }
        return false;
    }
}