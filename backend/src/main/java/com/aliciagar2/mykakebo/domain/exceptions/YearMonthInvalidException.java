package com.aliciagar2.mykakebo.domain.exceptions;

public class YearMonthInvalidException extends RuntimeException {
    public YearMonthInvalidException(String message) {
        super(message);
    }

    public YearMonthInvalidException(String message, Throwable cause) {
        super(message, cause);
    }
}
