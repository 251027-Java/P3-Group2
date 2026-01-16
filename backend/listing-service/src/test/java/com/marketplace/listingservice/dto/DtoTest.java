package com.marketplace.listingservice.dto;

import com.marketplace.listingservice.entity.Listing;
import com.marketplace.listingservice.entity.ListingStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for DTO classes.
 */
class DtoTest {

    @Test
    @DisplayName("ListingResponse should convert from entity correctly")
    void listingResponseFromEntity() {
        LocalDateTime now = LocalDateTime.now();
        
        Listing listing = Listing.builder()
                .listingId(1L)
                .ownerUserId(100L)
                .cardId(200L)
                .conditionRating(8)
                .listingStatus(ListingStatus.ACTIVE)
                .createdAt(now)
                .build();

        ListingResponse response = ListingResponse.fromEntity(listing);

        assertThat(response.getListingId()).isEqualTo(1L);
        assertThat(response.getOwnerUserId()).isEqualTo(100L);
        assertThat(response.getCardId()).isEqualTo(200L);
        assertThat(response.getConditionRating()).isEqualTo(8);
        assertThat(response.getListingStatus()).isEqualTo(ListingStatus.ACTIVE);
        assertThat(response.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("CreateListingRequest should build correctly")
    void createListingRequestBuilder() {
        CreateListingRequest request = CreateListingRequest.builder()
                .ownerUserId(100L)
                .cardId(200L)
                .conditionRating(8)
                .listingStatus(ListingStatus.ACTIVE)
                .build();

        assertThat(request.getOwnerUserId()).isEqualTo(100L);
        assertThat(request.getCardId()).isEqualTo(200L);
        assertThat(request.getConditionRating()).isEqualTo(8);
        assertThat(request.getListingStatus()).isEqualTo(ListingStatus.ACTIVE);
    }

    @Test
    @DisplayName("UpdateListingRequest should build correctly")
    void updateListingRequestBuilder() {
        UpdateListingRequest request = UpdateListingRequest.builder()
                .conditionRating(9)
                .listingStatus(ListingStatus.CANCELLED)
                .build();

        assertThat(request.getConditionRating()).isEqualTo(9);
        assertThat(request.getListingStatus()).isEqualTo(ListingStatus.CANCELLED);
    }

    @Test
    @DisplayName("ErrorResponse should build correctly")
    void errorResponseBuilder() {
        LocalDateTime now = LocalDateTime.now();
        
        ErrorResponse error = ErrorResponse.builder()
                .status(404)
                .message("Not found")
                .path("/api/listings/1")
                .timestamp(now)
                .build();

        assertThat(error.getStatus()).isEqualTo(404);
        assertThat(error.getMessage()).isEqualTo("Not found");
        assertThat(error.getPath()).isEqualTo("/api/listings/1");
        assertThat(error.getTimestamp()).isEqualTo(now);
    }

    @Test
    @DisplayName("ErrorResponse constructor should set timestamp")
    void errorResponseConstructor() {
        ErrorResponse error = new ErrorResponse(400, "Bad request", "/api/listings");

        assertThat(error.getStatus()).isEqualTo(400);
        assertThat(error.getMessage()).isEqualTo("Bad request");
        assertThat(error.getPath()).isEqualTo("/api/listings");
        assertThat(error.getTimestamp()).isNotNull();
    }
}
