package com.querifylabs.dbcourse.executor;

/**
 * The main driver of query execution. Represents an interface for Volcano-style iterator for the execution tree.
 */
public interface ExecutionNode extends AutoCloseable {
    /**
     * Gets next data page from the underlying operator. The returned page must be {@link DataPage#close()}d when
     * no longer needed.
     * @return Data page instance or {@code null} when no more data is available.
     */
    DataPage getNextPage() throws ExecutorException;
}
