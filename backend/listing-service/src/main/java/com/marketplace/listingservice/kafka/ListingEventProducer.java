// Generated with Assistance By Clause Opus 4.5
// Reviewed and modified by Marcus Wright 

package com.marketplace.listingservice.kafka;

import com.marketplace.listingservice.entity.Listing;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

/**
 * Kafka producer for listing events.
 * Publishes listing-related events to Kafka topics for event-driven communication.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ListingEventProducer {

    private final KafkaTemplate<String, ListingEvent> kafkaTemplate;

    @Value("${kafka.topics.listing-created:listing-created}")
    private String listingCreatedTopic;

    @Value("${kafka.topics.listing-updated:listing-updated}")
    private String listingUpdatedTopic;

    @Value("${kafka.topics.listing-status-changed:listing-status-changed}")
    private String listingStatusChangedTopic;

    @Value("${kafka.topics.listing-deleted:listing-deleted}")
    private String listingDeletedTopic;

    /**
     * Sends a listing created event to Kafka.
     *
     * @param listing the created listing
     */
    public void sendListingCreatedEvent(Listing listing) {
        ListingEvent event = buildEventFromListing(listing, "LISTING_CREATED");
        sendEvent(listingCreatedTopic, listing.getListingId().toString(), event);
    }

    /**
     * Sends a listing updated event to Kafka.
     *
     * @param listing the updated listing
     */
    public void sendListingUpdatedEvent(Listing listing) {
        ListingEvent event = buildEventFromListing(listing, "LISTING_UPDATED");
        sendEvent(listingUpdatedTopic, listing.getListingId().toString(), event);
    }

    /**
     * Sends a listing status changed event to Kafka.
     *
     * @param listing the listing with changed status
     * @param newStatus the new status
     */
    public void sendListingStatusChangedEvent(Listing listing, String newStatus) {
        ListingEvent event = buildEventFromListing(listing, "LISTING_STATUS_CHANGED");
        event.setAdditionalInfo("Status changed to: " + newStatus);
        sendEvent(listingStatusChangedTopic, listing.getListingId().toString(), event);
    }

    /**
     * Sends a listing deleted event to Kafka.
     *
     * @param listingId the ID of the deleted listing
     */
    public void sendListingDeletedEvent(Long listingId) {
        ListingEvent event = ListingEvent.builder()
                .eventType("LISTING_DELETED")
                .listingId(listingId)
                .timestamp(LocalDateTime.now())
                .build();
        sendEvent(listingDeletedTopic, listingId.toString(), event);
    }

    private ListingEvent buildEventFromListing(Listing listing, String eventType) {
        return ListingEvent.builder()
                .eventType(eventType)
                .listingId(listing.getListingId())
                .ownerUserId(listing.getOwnerUserId())
                .cardId(listing.getCardId())
                .conditionRating(listing.getConditionRating())
                .listingStatus(listing.getListingStatus().getValue())
                .timestamp(LocalDateTime.now())
                .build();
    }

    private void sendEvent(String topic, String key, ListingEvent event) {
        log.info("Sending event to topic {}: {}", topic, event);
        
        CompletableFuture<SendResult<String, ListingEvent>> future = 
                kafkaTemplate.send(topic, key, event);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Event sent successfully to topic {} with offset {}", 
                        topic, result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send event to topic {}: {}", topic, ex.getMessage(), ex);
            }
        });
    }
}
