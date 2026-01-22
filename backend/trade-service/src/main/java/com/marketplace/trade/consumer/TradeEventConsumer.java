// Generated with assistance from Cursor
// Reviewed and modified by Matt Selle
package com.marketplace.trade.consumer;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.marketplace.trade.service.TradeService;

import java.util.Map;

/**
 * Kafka consumer for trade-related events
 */
@Component
@Slf4j
public class TradeEventConsumer {

    private final TradeService tradeService;

    @Autowired
    public TradeEventConsumer(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @KafkaListener(
        topics = "listing-deleted",
        groupId = "trade-service-group"
    )
    public void consumeListingDeletedEvent(Map<String, Object> event) {
        log.info("Received listing deleted event: {}", event);

        try {
            Object listingIdObj = event.get("listingId");

            if (listingIdObj == null) {
                log.warn("listingId missing from event: {}", event);
                return;
            }

            Long listingId = Long.valueOf(listingIdObj.toString());

            tradeService.handleListingDeleted(listingId);

        } catch (Exception ex) {
            log.error(
                "Failed to process listing deleted event: {}",
                event,
                ex
            );
            throw ex; // enables Kafka retry / DLQ
        }
    }

    @KafkaListener(topics = "user-events", groupId = "trade-service-group")
    public void consumeUserEvent(Map<String, Object> event) {
        log.info("Received user event: {}", event);
    
        try {
            String eventType = (String) event.get("eventType");
    
            if (!"USER_DELETED".equals(eventType)) {
                return; // Ignore other user events
            }
    
            Object userIdObj = event.get("userId");
    
            if (userIdObj == null) {
                log.warn("USER_DELETED event missing userId: {}", event);
                return;
            }
    
            Long userId = Long.valueOf(userIdObj.toString());
    
            tradeService.handleUserDeleted(userId);
    
        } catch (Exception ex) {
            log.error("Failed to process user event: {}", event, ex);
            throw ex; // enables Kafka retry / DLQ
        }
    }    
}
