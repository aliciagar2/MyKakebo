package com.aliciagar2.mykakebo.domain.exceptions;

public class ExpenseConcurrentlyModifiedException extends RuntimeException {
    public ExpenseConcurrentlyModifiedException(String message) {
        super(message);
    }

    public ExpenseConcurrentlyModifiedException(String message, Throwable cause) {
        super(message, cause);
    }
}
