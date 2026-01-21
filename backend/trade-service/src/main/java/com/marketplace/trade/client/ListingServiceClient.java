// Generated with assistance from Cursor
// Reviewed and modified by Matt Selle
package com.marketplace.trade.client;

import com.marketplace.trade.client.dto.ListingResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

/**
 * Feign client for communicating with listing-service
 */
@FeignClient(name = "listing-service")
public interface ListingServiceClient {

    @GetMapping("/api/listings/{listingId}")
    ListingResponse getListing(@PathVariable("listingId") Long listingId);

    @PutMapping("/api/listings/{listingId}/complete")
    ListingResponse updateListingStatusToComplete(@PathVariable("listingId") Long listingId);
}
