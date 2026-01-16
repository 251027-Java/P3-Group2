// Generated with Assistance By Clause Opus 4.5
// Reviewed and modified by Marcus Wright 

package com.marketplace.listingservice.exception;

/**
 * Exception thrown when a card is not found via the card service.
 */
public class CardNotFoundException extends RuntimeException {

    public CardNotFoundException(String message) {
        super(message);
    }

    public CardNotFoundException(Long cardId) {
        super("Card not found with ID: " + cardId);
    }
}
