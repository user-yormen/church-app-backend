package com.church.anglican.backend.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ApiErrorResponse error = baseError(HttpStatus.BAD_REQUEST, "Validation failed", request);
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            error.getDetails().add(fieldError.getField() + ": " + fieldError.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        ApiErrorResponse error = baseError(HttpStatus.BAD_REQUEST, "Validation failed", request);
        ex.getConstraintViolations().forEach(v -> error.getDetails().add(v.getPropertyPath() + ": " + v.getMessage()));
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> handleRuntime(RuntimeException ex, HttpServletRequest request) {
        ApiErrorResponse error = baseError(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(NotFoundException ex, HttpServletRequest request) {
        ApiErrorResponse error = baseError(HttpStatus.NOT_FOUND, ex.getMessage(), request);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleOther(Exception ex, HttpServletRequest request) {
        ApiErrorResponse error = baseError(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", request);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private ApiErrorResponse baseError(HttpStatus status, String message, HttpServletRequest request) {
        ApiErrorResponse error = new ApiErrorResponse();
        error.setStatus(status.value());
        error.setError(status.getReasonPhrase());
        error.setMessage(message);
        error.setPath(request.getRequestURI());
        return error;
    }
}
