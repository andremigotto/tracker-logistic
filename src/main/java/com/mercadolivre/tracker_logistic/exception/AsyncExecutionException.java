package com.mercadolivre.tracker_logistic.exception;

public class AsyncExecutionException extends RuntimeException {

    public AsyncExecutionException(String message) {
        super(message);
    }

    public AsyncExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
