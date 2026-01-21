// Generated with assistance from Claude Opus 4.5
// Reviewed and modified by Marcus Wright

package com.marketplace.trade.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for custom exception classes
 */
class ExceptionClassesTest {

    // ------------------- ResourceNotFoundException Tests -------------------

    @Test
    @DisplayName("ResourceNotFoundException should store message correctly")
    void resourceNotFoundException_WithMessage() {
        String message = "Resource not found";
        ResourceNotFoundException ex = new ResourceNotFoundException(message);

        assertEquals(message, ex.getMessage());
    }

    @Test
    @DisplayName("ResourceNotFoundException should store message and cause")
    void resourceNotFoundException_WithMessageAndCause() {
        String message = "Resource not found";
        Throwable cause = new RuntimeException("Underlying error");
        ResourceNotFoundException ex = new ResourceNotFoundException(message, cause);

        assertEquals(message, ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    @Test
    @DisplayName("ResourceNotFoundException should extend RuntimeException")
    void resourceNotFoundException_ExtendsRuntimeException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("test");
        assertTrue(ex instanceof RuntimeException);
    }

    // ------------------- TradeException Tests -------------------

    @Test
    @DisplayName("TradeException should store message correctly")
    void tradeException_WithMessage() {
        String message = "Trade operation failed";
        TradeException ex = new TradeException(message);

        assertEquals(message, ex.getMessage());
    }

    @Test
    @DisplayName("TradeException should store message and cause")
    void tradeException_WithMessageAndCause() {
        String message = "Trade operation failed";
        Throwable cause = new IllegalStateException("Invalid state");
        TradeException ex = new TradeException(message, cause);

        assertEquals(message, ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    @Test
    @DisplayName("TradeException should extend RuntimeException")
    void tradeException_ExtendsRuntimeException() {
        TradeException ex = new TradeException("test");
        assertTrue(ex instanceof RuntimeException);
    }

    @Test
    @DisplayName("TradeException with nested cause chain")
    void tradeException_NestedCauseChain() {
        Throwable rootCause = new IllegalArgumentException("Invalid argument");
        Throwable intermediateCause = new IllegalStateException("Invalid state", rootCause);
        TradeException ex = new TradeException("Trade failed", intermediateCause);

        assertEquals(intermediateCause, ex.getCause());
        assertEquals(rootCause, ex.getCause().getCause());
    }

    @Test
    @DisplayName("ResourceNotFoundException with null message")
    void resourceNotFoundException_NullMessage() {
        ResourceNotFoundException ex = new ResourceNotFoundException(null);
        assertNull(ex.getMessage());
    }

    @Test
    @DisplayName("TradeException with null message")
    void tradeException_NullMessage() {
        TradeException ex = new TradeException(null);
        assertNull(ex.getMessage());
    }

    @Test
    @DisplayName("ResourceNotFoundException with empty message")
    void resourceNotFoundException_EmptyMessage() {
        ResourceNotFoundException ex = new ResourceNotFoundException("");
        assertEquals("", ex.getMessage());
    }

    @Test
    @DisplayName("TradeException with empty message")
    void tradeException_EmptyMessage() {
        TradeException ex = new TradeException("");
        assertEquals("", ex.getMessage());
    }
}
