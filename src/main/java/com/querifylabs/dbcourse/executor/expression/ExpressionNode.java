package com.querifylabs.dbcourse.executor.expression;

import com.querifylabs.dbcourse.executor.DataPage;
import com.querifylabs.dbcourse.executor.ExecutorException;
import org.apache.arrow.vector.ValueVector;

public interface ExpressionNode {
    ValueVector evaluate(DataPage page) throws ExecutorException;
}
