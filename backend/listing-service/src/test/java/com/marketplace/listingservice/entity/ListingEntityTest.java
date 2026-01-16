// Generated with Assistance By Clause Opus 4.5
// Reviewed and modified by Marcus Wright 

package com.marketplace.listingservice.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for Listing entity and ListingStatus enum.
 */
class ListingEntityTest {

    @Test
    @DisplayName("Should create listing with builder")
    void createListingWithBuilder() {
        LocalDateTime now = LocalDateTime.now();
        
        Listing listing = Listing.builder()
                .listingId(1L)
                .ownerUserId(100L)
                .cardId(200L)
                .conditionRating(8)
                .listingStatus(ListingStatus.ACTIVE)
                .createdAt(now)
                .build();

        assertThat(listing.getListingId()).isEqualTo(1L);
        assertThat(listing.getOwnerUserId()).isEqualTo(100L);
        assertThat(listing.getCardId()).isEqualTo(200L);
        assertThat(listing.getConditionRating()).isEqualTo(8);
        assertThat(listing.getListingStatus()).isEqualTo(ListingStatus.ACTIVE);
        assertThat(listing.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should create listing with no-args constructor")
    void createListingWithNoArgsConstructor() {
        Listing listing = new Listing();
        
        assertThat(listing.getListingId()).isNull();
        assertThat(listing.getOwnerUserId()).isNull();
        assertThat(listing.getCardId()).isNull();
    }

    @Test
    @DisplayName("Should set and get listing properties")
    void setAndGetListingProperties() {
        Listing listing = new Listing();
        
        listing.setListingId(1L);
        listing.setOwnerUserId(100L);
        listing.setCardId(200L);
        listing.setConditionRating(7);
        listing.setListingStatus(ListingStatus.COMPLETED);

        assertThat(listing.getListingId()).isEqualTo(1L);
        assertThat(listing.getOwnerUserId()).isEqualTo(100L);
        assertThat(listing.getCardId()).isEqualTo(200L);
        assertThat(listing.getConditionRating()).isEqualTo(7);
        assertThat(listing.getListingStatus()).isEqualTo(ListingStatus.COMPLETED);
    }

    @Test
    @DisplayName("Should set createdAt and status on prePersist")
    void prePersistSetsDefaults() {
        Listing listing = new Listing();
        listing.onCreate();

        assertThat(listing.getCreatedAt()).isNotNull();
        assertThat(listing.getListingStatus()).isEqualTo(ListingStatus.ACTIVE);
    }

    @Test
    @DisplayName("ListingStatus should return correct value")
    void listingStatusGetValue() {
        assertThat(ListingStatus.ACTIVE.getValue()).isEqualTo("active");
        assertThat(ListingStatus.COMPLETED.getValue()).isEqualTo("completed");
        assertThat(ListingStatus.CANCELLED.getValue()).isEqualTo("cancelled");
    }

    @Test
    @DisplayName("ListingStatus should parse from value")
    void listingStatusFromValue() {
        assertThat(ListingStatus.fromValue("active")).isEqualTo(ListingStatus.ACTIVE);
        assertThat(ListingStatus.fromValue("completed")).isEqualTo(ListingStatus.COMPLETED);
        assertThat(ListingStatus.fromValue("cancelled")).isEqualTo(ListingStatus.CANCELLED);
        assertThat(ListingStatus.fromValue("ACTIVE")).isEqualTo(ListingStatus.ACTIVE);
    }

    @Test
    @DisplayName("ListingStatus should throw exception for invalid value")
    void listingStatusFromValueInvalid() {
        assertThatThrownBy(() -> ListingStatus.fromValue("invalid"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown listing status");
    }

    @Test
    @DisplayName("Listing should have correct equals and hashCode")
    void listingEqualsAndHashCode() {
        Listing listing1 = Listing.builder()
                .listingId(1L)
                .ownerUserId(100L)
                .cardId(200L)
                .build();

        Listing listing2 = Listing.builder()
                .listingId(1L)
                .ownerUserId(100L)
                .cardId(200L)
                .build();

        assertThat(listing1).isEqualTo(listing2);
        assertThat(listing1.hashCode()).isEqualTo(listing2.hashCode());
    }

    @Test
    @DisplayName("Listing should have correct toString")
    void listingToString() {
        Listing listing = Listing.builder()
                .listingId(1L)
                .ownerUserId(100L)
                .cardId(200L)
                .conditionRating(8)
                .listingStatus(ListingStatus.ACTIVE)
                .build();

        String toStringResult = listing.toString();
        
        assertThat(toStringResult).contains("listingId=1");
        assertThat(toStringResult).contains("ownerUserId=100");
        assertThat(toStringResult).contains("cardId=200");
    }
}
