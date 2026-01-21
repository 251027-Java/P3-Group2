// Generated with assistance from Claude Opus 4.5
// Reviewed and modified by Marcus Wright

package com.marketplace.trade.consumer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

/**
 * Unit tests for TradeEventConsumer
 */
@ExtendWith(MockitoExtension.class)
class TradeEventConsumerTest {

    private TradeEventConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new TradeEventConsumer();
    }

    @Test
    @DisplayName("Should consume listing event without errors")
    void consumeListingEvent_Success() {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "LISTING_CREATED");
        event.put("listingId", 1L);
        event.put("ownerId", 100L);

        // Should not throw any exception
        assertDoesNotThrow(() -> consumer.consumeListingEvent(event));
    }

    @Test
    @DisplayName("Should consume user event without errors")
    void consumeUserEvent_Success() {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "USER_CREATED");
        event.put("userId", 1L);
        event.put("username", "testuser");

        // Should not throw any exception
        assertDoesNotThrow(() -> consumer.consumeUserEvent(event));
    }

    @Test
    @DisplayName("Should handle empty listing event")
    void consumeListingEvent_EmptyEvent() {
        Map<String, Object> event = new HashMap<>();

        assertDoesNotThrow(() -> consumer.consumeListingEvent(event));
    }

    @Test
    @DisplayName("Should handle empty user event")
    void consumeUserEvent_EmptyEvent() {
        Map<String, Object> event = new HashMap<>();

        assertDoesNotThrow(() -> consumer.consumeUserEvent(event));
    }

    @Test
    @DisplayName("Should handle listing event with null values")
    void consumeListingEvent_NullValues() {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", null);
        event.put("listingId", null);

        assertDoesNotThrow(() -> consumer.consumeListingEvent(event));
    }

    @Test
    @DisplayName("Should handle user event with null values")
    void consumeUserEvent_NullValues() {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", null);
        event.put("userId", null);

        assertDoesNotThrow(() -> consumer.consumeUserEvent(event));
    }

    @Test
    @DisplayName("Should handle listing deleted event")
    void consumeListingEvent_ListingDeleted() {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "LISTING_DELETED");
        event.put("listingId", 1L);

        assertDoesNotThrow(() -> consumer.consumeListingEvent(event));
    }

    @Test
    @DisplayName("Should handle user deleted event")
    void consumeUserEvent_UserDeleted() {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "USER_DELETED");
        event.put("userId", 1L);

        assertDoesNotThrow(() -> consumer.consumeUserEvent(event));
    }
}
