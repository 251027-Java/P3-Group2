// Generated with assistance from Claude Opus 4.5
// Reviewed and modified by Marcus Wright

package com.marketplace.trade.exception;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import static org.mockito.Mockito.*;

/**
 * Unit tests for GlobalExceptionHandler
 */
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Should handle ResourceNotFoundException and return 404")
    void handleResourceNotFoundException_Returns404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Trade not found: 123");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleResourceNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Trade not found: 123", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    @DisplayName("Should handle TradeException and return 400")
    void handleTradeException_Returns400() {
        TradeException ex = new TradeException("Cannot create trade for your own listing");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleTradeException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Cannot create trade for your own listing", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    @DisplayName("Should handle MissingServletRequestParameterException and return 400")
    void handleMissingRequestParam_Returns400() {
        MissingServletRequestParameterException ex = new MissingServletRequestParameterException("listingOwnerId", "Long");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleMissingRequestParam(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("listingOwnerId"));
    }

    @Test
    @DisplayName("Should handle MethodArgumentTypeMismatchException and return 400")
    void handleTypeMismatch_Returns400() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getCause()).thenReturn(new IllegalArgumentException("invalid value"));

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleTypeMismatch(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("Invalid value for parameter"));
    }

    @Test
    @DisplayName("Should handle generic Exception and return 500")
    void handleGenericException_Returns500() throws Exception {
        Exception ex = new RuntimeException("Unexpected error");
        HttpServletRequest request = mock(HttpServletRequest.class);

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleGenericException(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
    }

    @Test
    @DisplayName("ErrorResponse should allow setting status")
    void errorResponse_SetStatus() {
        GlobalExceptionHandler.ErrorResponse errorResponse = new GlobalExceptionHandler.ErrorResponse(400, "test", null);
        errorResponse.setStatus(500);
        assertEquals(500, errorResponse.getStatus());
    }

    @Test
    @DisplayName("ErrorResponse should allow setting message")
    void errorResponse_SetMessage() {
        GlobalExceptionHandler.ErrorResponse errorResponse = new GlobalExceptionHandler.ErrorResponse(400, "test", null);
        errorResponse.setMessage("new message");
        assertEquals("new message", errorResponse.getMessage());
    }

    @Test
    @DisplayName("ErrorResponse should allow setting timestamp")
    void errorResponse_SetTimestamp() {
        GlobalExceptionHandler.ErrorResponse errorResponse = new GlobalExceptionHandler.ErrorResponse(400, "test", null);
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        errorResponse.setTimestamp(now);
        assertEquals(now, errorResponse.getTimestamp());
    }
}
