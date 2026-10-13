package com.aliciagar2.mykakebo.domain.exceptions;

public class MonthlyBudgetNotFoundException extends RuntimeException {
    public MonthlyBudgetNotFoundException(String message) {
        super(message);
    }

    public MonthlyBudgetNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
