package com.marketplace.auth.exception;

import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Catch 409 Conflict (User already exists)
    @ExceptionHandler(FeignException.Conflict.class)
    public ResponseEntity<?> handleFeignConflict(FeignException.Conflict e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", "The resource already exists", "details", e.getMessage()));
    }

    // Catch 404 Not Found (User doesn't exist)
    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<?> handleFeignNotFound(FeignException.NotFound e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "The requested resource was not found"));
    }

    // Catch all other Feign errors (500, 503, etc.)
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<?> handleGeneralFeignException(FeignException e) {
        return ResponseEntity.status(e.status() > 0 ? e.status() : 500)
                .body(Map.of("error", "Internal Service Communication Error", "status", e.status()));
    }

    @ExceptionHandler(InvalidLoginException.class)
    public ResponseEntity<String> handleException(InvalidLoginException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
