// Generated with assistance from Cursor
// Reviewed and modified by Matt Selle
package com.marketplace.trade.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign client for communicating with listing-service
 */
@FeignClient(name = "listing-service")
public interface ListingServiceClient {

    @GetMapping("/api/listings/{listingId}")
    ListingResponse getListing(@PathVariable("listingId") Long listingId);

    @PutMapping("/api/listings/{listingId}/complete")
    void updateListingStatusToComplete(@PathVariable("listingId") Long listingId);

    class ListingResponse {
        private Long listingId;
        private Long ownerUserId;
        private Long cardId;
        private String listingStatus;

        public Long getListingId() {
            return listingId;
        }

        public void setListingId(Long listingId) {
            this.listingId = listingId;
        }

        public Long getOwnerUserId() {
            return ownerUserId;
        }

        public void setOwnerUserId(Long ownerUserId) {
            this.ownerUserId = ownerUserId;
        }

        public Long getCardId() {
            return cardId;
        }

        public void setCardId(Long cardId) {
            this.cardId = cardId;
        }

        public String getListingStatus() {
            return listingStatus;
        }

        public void setListingStatus(String listingStatus) {
            this.listingStatus = listingStatus;
        }
    }

    class ListingStatusUpdate {
        private String status;

        public ListingStatusUpdate() {}

        public ListingStatusUpdate(String status) {
            this.status = status;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
