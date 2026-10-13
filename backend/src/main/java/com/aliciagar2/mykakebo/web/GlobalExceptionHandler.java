package com.aliciagar2.mykakebo.web;


import com.aliciagar2.mykakebo.domain.exceptions.ExpenseNotFoundException;
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

    private ResponseEntity<ErrorResponse> buildResponse(ErrorCode code) {
        return ResponseEntity.status(code.getHttpStatus())
                .body(new ErrorResponse(code.name(), code.getMessage(), null));
    }

}
