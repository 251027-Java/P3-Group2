package com.marketplace.listingservice.exception;

/**
 * Exception thrown when an invalid listing operation is attempted.
 */
public class InvalidListingOperationException extends RuntimeException {

    public InvalidListingOperationException(String message) {
        super(message);
    }
}
