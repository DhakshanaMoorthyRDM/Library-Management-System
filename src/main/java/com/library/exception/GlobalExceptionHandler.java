package com.library.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BorrowException.class)
    public ResponseEntity<ErrorResponse> handleBorrowException(
            BorrowException exception,
            HttpServletRequest request) {

        ErrorResponse response =
                new ErrorResponse(
                        exception.getMessage(),
                        request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }
}
