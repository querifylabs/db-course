package com.querifylabs.dbcourse.executor;

public abstract class BaseExecutionNode implements ExecutionNode {
    protected final ExecutionContext executionContext;

    public BaseExecutionNode(ExecutionContext executionContext) {
        this.executionContext = executionContext;
    }
}
