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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

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

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequestParameter(MethodArgumentTypeMismatchException ex) {
        return buildResponse(ErrorCode.INVALID_REQUEST_PARAMETER);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return handleExceptionInternal(ex, errorBody(ErrorCode.MALFORMED_REQUEST_BODY), headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<FieldViolation> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new FieldViolation(error.getField(), error.getDefaultMessage()))
                .toList();
        ErrorCode code = ErrorCode.VALIDATION_ERROR;
        ErrorResponse body = new ErrorResponse(code.name(), code.getMessage(), violations);
        return handleExceptionInternal(ex, body, headers, status, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex);
        return buildResponse(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        if (statusCode.is5xxServerError()) {
            log.error("Unexpected error", ex);
        }
        Object responseBody = body != null
                ? body
                : new ErrorResponse(HttpStatus.valueOf(statusCode.value()).name(), ex.getMessage(), null);
        return ResponseEntity.status(statusCode).headers(headers).body(responseBody);
    }

    private ErrorResponse errorBody(ErrorCode code) {
        return new ErrorResponse(code.name(), code.getMessage(), null);
    }

    private ResponseEntity<ErrorResponse> buildResponse(ErrorCode code) {
        return ResponseEntity.status(code.getHttpStatus()).body(errorBody(code));
    }
}