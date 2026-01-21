package com.marketplace.auth.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Unit tests for GlobalExceptionHandler.
 */
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Should return 401 UNAUTHORIZED for InvalidLoginException")
    void handleInvalidLoginException_ReturnsUnauthorized() {
        // Arrange
        InvalidLoginException exception = new InvalidLoginException("Invalid credentials");

        // Act
        ResponseEntity<String> response = exceptionHandler.handleException(exception);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody());
    }

    @Test
    @DisplayName("Should include exception message in response body")
    void handleInvalidLoginException_IncludesMessageInBody() {
        // Arrange
        String errorMessage = "User not found or incorrect password";
        InvalidLoginException exception = new InvalidLoginException(errorMessage);

        // Act
        ResponseEntity<String> response = exceptionHandler.handleException(exception);

        // Assert
        assertEquals(errorMessage, response.getBody());
    }

    @Test
    @DisplayName("Should handle exception with empty message")
    void handleInvalidLoginException_EmptyMessage_ReturnsUnauthorized() {
        // Arrange
        InvalidLoginException exception = new InvalidLoginException("");

        // Act
        ResponseEntity<String> response = exceptionHandler.handleException(exception);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("", response.getBody());
    }
}
