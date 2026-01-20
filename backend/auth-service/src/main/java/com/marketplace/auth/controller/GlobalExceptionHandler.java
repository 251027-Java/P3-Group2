package com.marketplace.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.marketplace.auth.exception.InvalidLoginException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(InvalidLoginException.class)
    public ResponseEntity<String> handleException(InvalidLoginException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }
}
