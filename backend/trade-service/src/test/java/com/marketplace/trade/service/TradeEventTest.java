// Generated with assistance from Claude Opus 4.5
// Reviewed and modified by Marcus Wright

package com.marketplace.trade.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for TradeService.TradeEvent inner class
 */
class TradeEventTest {

    @Test
    @DisplayName("Should create TradeEvent with all args constructor")
    void tradeEvent_AllArgsConstructor() {
        TradeService.TradeEvent event = new TradeService.TradeEvent("TRADE_CREATED", 1L, 2L, 3L);

        assertEquals("TRADE_CREATED", event.getEventType());
        assertEquals(1L, event.getTradeId());
        assertEquals(2L, event.getListingId());
        assertEquals(3L, event.getRequestingUserId());
        assertNotNull(event.getTimestamp());
    }

    @Test
    @DisplayName("Should create TradeEvent with no-args constructor")
    void tradeEvent_NoArgsConstructor() {
        TradeService.TradeEvent event = new TradeService.TradeEvent();

        assertNull(event.getEventType());
        assertNull(event.getTradeId());
        assertNull(event.getListingId());
        assertNull(event.getRequestingUserId());
        assertNotNull(event.getTimestamp());
    }

    @Test
    @DisplayName("Should set and get eventType")
    void tradeEvent_SetEventType() {
        TradeService.TradeEvent event = new TradeService.TradeEvent();
        event.setEventType("TRADE_ACCEPTED");

        assertEquals("TRADE_ACCEPTED", event.getEventType());
    }

    @Test
    @DisplayName("Should set and get tradeId")
    void tradeEvent_SetTradeId() {
        TradeService.TradeEvent event = new TradeService.TradeEvent();
        event.setTradeId(100L);

        assertEquals(100L, event.getTradeId());
    }

    @Test
    @DisplayName("Should set and get listingId")
    void tradeEvent_SetListingId() {
        TradeService.TradeEvent event = new TradeService.TradeEvent();
        event.setListingId(200L);

        assertEquals(200L, event.getListingId());
    }

    @Test
    @DisplayName("Should set and get requestingUserId")
    void tradeEvent_SetRequestingUserId() {
        TradeService.TradeEvent event = new TradeService.TradeEvent();
        event.setRequestingUserId(300L);

        assertEquals(300L, event.getRequestingUserId());
    }

    @Test
    @DisplayName("Should set and get timestamp")
    void tradeEvent_SetTimestamp() {
        TradeService.TradeEvent event = new TradeService.TradeEvent();
        Long timestamp = System.currentTimeMillis();
        event.setTimestamp(timestamp);

        assertEquals(timestamp, event.getTimestamp());
    }

    @Test
    @DisplayName("Should create event for TRADE_DECLINED")
    void tradeEvent_DeclinedEvent() {
        TradeService.TradeEvent event = new TradeService.TradeEvent("TRADE_DECLINED", 5L, 10L, 15L);

        assertEquals("TRADE_DECLINED", event.getEventType());
        assertEquals(5L, event.getTradeId());
        assertEquals(10L, event.getListingId());
        assertEquals(15L, event.getRequestingUserId());
    }

    @Test
    @DisplayName("Timestamp should be auto-set on creation")
    void tradeEvent_TimestampAutoSet() {
        long before = System.currentTimeMillis();
        TradeService.TradeEvent event = new TradeService.TradeEvent("TEST", 1L, 1L, 1L);
        long after = System.currentTimeMillis();

        assertTrue(event.getTimestamp() >= before);
        assertTrue(event.getTimestamp() <= after);
    }
}
