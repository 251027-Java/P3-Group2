// Generated with assistance from Cursor
// Reviewed and modified by Matt Selle
package com.marketplace.trade.exception;

/**
 * Custom exception for trade-related business logic errors
 */
public class TradeException extends RuntimeException {
    
    public TradeException(String message) {
        super(message);
    }
    
    public TradeException(String message, Throwable cause) {
        super(message, cause);
    }
}
