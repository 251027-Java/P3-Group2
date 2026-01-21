// Generated with assistance from Cursor
// Reviewed and modified by Matt Selle
package com.marketplace.trade.client;

import java.time.LocalDateTime;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Feign client for communicating with listing-service
 */
@FeignClient(name = "listing-service")
public interface ListingServiceClient {

    @GetMapping("/api/listings/{listingId}")
    ListingResponse getListing(@PathVariable("listingId") Long listingId);

    @PutMapping("/api/listings/{listingId}/complete")
    ListingResponse updateListingStatusToComplete(@PathVariable("listingId") Long listingId);

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class ListingResponse {
        private Long listingId;
        private Long ownerUserId;
        private Long cardId;
        private Integer conditionRating;
        private ListingStatus listingStatus;
        private LocalDateTime createdAt;
    }

    public enum ListingStatus {
        ACTIVE("active"),
        COMPLETED("completed"),
        CANCELLED("cancelled");
    
        private final String value;
    
        ListingStatus(String value) {
            this.value = value;
        }
    
        public String getValue() {
            return value;
        }
    
        public static ListingStatus fromValue(String value) {
            for (ListingStatus status : ListingStatus.values()) {
                if (status.value.equalsIgnoreCase(value)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unknown listing status: " + value);
        }
    }

}
