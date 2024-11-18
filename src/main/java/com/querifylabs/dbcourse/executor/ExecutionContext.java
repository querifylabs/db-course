package com.querifylabs.dbcourse.executor;

import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;

public class ExecutionContext implements AutoCloseable {
    private final BufferAllocator allocator = new RootAllocator();

    public BufferAllocator getAllocator() {
        return allocator;
    }

    @Override
    public void close() throws Exception {
        allocator.close();
    }
}
