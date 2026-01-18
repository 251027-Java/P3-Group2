// Generated with Assistance By Clause Opus 4.5
// Reviewed and modified by Marcus Wright

package com.marketplace.listingservice.exception;

/**
 * Exception thrown when a requested listing is not found.
 */
public class ListingNotFoundException extends RuntimeException {

    public ListingNotFoundException(String message) {
        super(message);
    }

    public ListingNotFoundException(Long listingId) {
        super("Listing not found with ID: " + listingId);
    }
}
