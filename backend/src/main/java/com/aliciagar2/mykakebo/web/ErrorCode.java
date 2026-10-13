package com.aliciagar2.mykakebo.web;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    EXPENSE_NOT_FOUND(HttpStatus.NOT_FOUND, "Expense not found"),
    MONTHLY_BUDGET_NOT_FOUND(HttpStatus.NOT_FOUND, "Monthly budget not found"),
    MONTHLY_BUDGET_ALREADY_EXISTS(HttpStatus.CONFLICT, "Monthly budget already exists"),
    INVALID_YEAR_MONTH(HttpStatus.BAD_REQUEST, "Invalid year/month"),
    EXPENSE_DATE_OUTSIDE_MONTH(HttpStatus.BAD_REQUEST, "Expense date is outside the given month"),
    MALFORMED_REQUEST_BODY(HttpStatus.BAD_REQUEST, "Malformed request body"),
    INVALID_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST, "Invalid request parameter"),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Request validation failed"),
    REQUEST_ERROR(HttpStatus.BAD_REQUEST, "The request could not be processed"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Something wrong happened");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
