// Generated with assistance from Cursor
// Reviewed and modified by Matt Selle
package com.marketplace.trade.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Kafka consumer for trade-related events
 */
@Component
@Slf4j
public class TradeEventConsumer {

    @KafkaListener(topics = "listing-events", groupId = "trade-service-group")
    public void consumeListingEvent(Map<String, Object> event) {
        log.info("Received listing event: {}", event);
        // Handle listing events if needed (e.g., when a listing is deleted, cancel related trades)
    }

    @KafkaListener(topics = "user-events", groupId = "trade-service-group")
    public void consumeUserEvent(Map<String, Object> event) {
        log.info("Received user event: {}", event);
        // Handle user events if needed
    }
}
