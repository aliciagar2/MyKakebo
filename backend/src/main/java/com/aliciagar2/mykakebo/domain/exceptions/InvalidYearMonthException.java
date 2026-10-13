package com.aliciagar2.mykakebo.domain.exceptions;

public class InvalidYearMonthException extends RuntimeException {
    public InvalidYearMonthException(String message) {
        super(message);
    }

    public InvalidYearMonthException(String message, Throwable cause) {
        super(message, cause);
    }
}
