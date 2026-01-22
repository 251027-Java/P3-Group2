// Generated with assistance from ChatGPT
// Reviewed and modified by Matt Selle

package com.marketplace.trade.consumer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

import com.marketplace.trade.service.TradeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

/**
 * Unit tests for TradeEventConsumer
 */
@ExtendWith(MockitoExtension.class)
class TradeEventConsumerTest {

    @Mock
    private TradeService tradeService;

    @InjectMocks
    private TradeEventConsumer consumer;

    @BeforeEach
    void setUp() {
        // Mockito handles injection
    }

    @Test
    @DisplayName("Should consume listing deleted event and call service")
    void consumeListingDeletedEvent_Success() {
        Map<String, Object> event = new HashMap<>();
        event.put("listingId", 1L);

        assertDoesNotThrow(() ->
            consumer.consumeListingDeletedEvent(event)
        );

        verify(tradeService, times(1)).handleListingDeleted(1L);
    }

    @Test
    @DisplayName("Should ignore listing deleted event with missing listingId")
    void consumeListingDeletedEvent_MissingListingId() {
        Map<String, Object> event = new HashMap<>();

        assertDoesNotThrow(() ->
            consumer.consumeListingDeletedEvent(event)
        );

        verify(tradeService, never()).handleListingDeleted(any());
    }

    @Test
    @DisplayName("Should ignore listing deleted event with null listingId")
    void consumeListingDeletedEvent_NullListingId() {
        Map<String, Object> event = new HashMap<>();
        event.put("listingId", null);

        assertDoesNotThrow(() ->
            consumer.consumeListingDeletedEvent(event)
        );

        verify(tradeService, never()).handleListingDeleted(any());
    }

    @Test
    @DisplayName("Should consume user event without errors")
    void consumeUserEvent_Success() {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "USER_CREATED");
        event.put("userId", 1L);

        assertDoesNotThrow(() ->
            consumer.consumeUserEvent(event)
        );
    }

    @Test
    @DisplayName("Should handle empty user event")
    void consumeUserEvent_EmptyEvent() {
        Map<String, Object> event = new HashMap<>();

        assertDoesNotThrow(() ->
            consumer.consumeUserEvent(event)
        );
    }
}
