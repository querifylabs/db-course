package com.querifylabs.dbcourse.executor;

public class ExecutorException extends Exception {
    public ExecutorException(String message) {
        super(message);
    }

    public ExecutorException(String message, Throwable cause) {
        super(message, cause);
    }
}
