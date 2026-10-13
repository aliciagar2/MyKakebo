package com.aliciagar2.mykakebo.domain.exceptions;

public class ExpenseDateOutsideMonthException extends RuntimeException {
    public ExpenseDateOutsideMonthException(String message) {
        super(message);
    }

    public ExpenseDateOutsideMonthException(String message, Throwable cause) {
        super(message, cause);
    }
}
