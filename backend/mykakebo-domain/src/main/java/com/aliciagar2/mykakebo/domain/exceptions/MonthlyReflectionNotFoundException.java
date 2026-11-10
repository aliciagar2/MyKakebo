package com.aliciagar2.mykakebo.domain.exceptions;

public class MonthlyReflectionNotFoundException extends RuntimeException {
    public MonthlyReflectionNotFoundException(String message) {
        super(message);
    }

    public MonthlyReflectionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
