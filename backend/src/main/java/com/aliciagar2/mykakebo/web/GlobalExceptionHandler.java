package com.aliciagar2.mykakebo.web;


import com.aliciagar2.mykakebo.domain.exceptions.*;
import com.aliciagar2.mykakebo.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExpenseNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleExpenseNotFound(ExpenseNotFoundException ex) {
        return buildResponse(ErrorCode.EXPENSE_NOT_FOUND);
    }

    @ExceptionHandler(MonthlyBudgetAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleMonthlyBudgetAlreadyExists(MonthlyBudgetAlreadyExistsException ex) {
        return buildResponse(ErrorCode.MONTHLY_BUDGET_ALREADY_EXISTS);
    }

    @ExceptionHandler(MonthlyBudgetNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMonthlyBudgetNotFoundException(MonthlyBudgetNotFoundException ex) {
        return buildResponse(ErrorCode.MONTHLY_BUDGET_NOT_FOUND);
    }

    @ExceptionHandler(InvalidYearMonthException.class)
    public ResponseEntity<ErrorResponse> handleInvalidYearMonth(InvalidYearMonthException ex) {
        return buildResponse(ErrorCode.INVALID_YEAR_MONTH);
    }

    @ExceptionHandler(ExpenseDateOutsideMonthException.class)
    public ResponseEntity<ErrorResponse> handleExpenseDateOutsideMonth(ExpenseDateOutsideMonthException ex) {
        return buildResponse(ErrorCode.EXPENSE_DATE_OUTSIDE_MONTH);
    }

    private ResponseEntity<ErrorResponse> buildResponse(ErrorCode code) {
        return ResponseEntity.status(code.getHttpStatus())
                .body(new ErrorResponse(code.name(), code.getMessage(), null));
    }

}
