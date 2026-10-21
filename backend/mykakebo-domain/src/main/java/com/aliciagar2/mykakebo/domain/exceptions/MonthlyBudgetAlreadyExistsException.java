package com.aliciagar2.mykakebo.domain.exceptions;

public class MonthlyBudgetAlreadyExistsException extends RuntimeException {
    public MonthlyBudgetAlreadyExistsException(String message) {
        super(message);
    }

    public MonthlyBudgetAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
