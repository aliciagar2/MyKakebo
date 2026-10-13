package com.aliciagar2.mykakebo.web;

import com.aliciagar2.mykakebo.domain.exceptions.ExpenseDateOutsideMonthException;
import com.aliciagar2.mykakebo.domain.exceptions.ExpenseNotFoundException;
import com.aliciagar2.mykakebo.domain.exceptions.InvalidYearMonthException;
import com.aliciagar2.mykakebo.domain.exceptions.MonthlyBudgetAlreadyExistsException;
import com.aliciagar2.mykakebo.domain.exceptions.MonthlyBudgetNotFoundException;
import com.aliciagar2.mykakebo.dto.ErrorResponse;
import com.aliciagar2.mykakebo.dto.FieldViolation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMalformedRequestBody(HttpMessageNotReadableException ex) {
        return buildResponse(ErrorCode.MALFORMED_REQUEST_BODY);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequestParameter(MethodArgumentTypeMismatchException ex) {
        return buildResponse(ErrorCode.INVALID_REQUEST_PARAMETER);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<FieldViolation> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new FieldViolation(error.getField(), error.getDefaultMessage()))
                .toList();
        ErrorCode code = ErrorCode.VALIDATION_ERROR;
        return ResponseEntity.status(code.getHttpStatus())
                .body(new ErrorResponse(code.name(), code.getMessage(), violations));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex);
        return buildResponse(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> buildResponse(ErrorCode code) {
        return ResponseEntity.status(code.getHttpStatus())
                .body(new ErrorResponse(code.name(), code.getMessage(), null));
    }
}