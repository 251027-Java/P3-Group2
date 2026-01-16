package com.marketplace.listingservice.repository;

import com.marketplace.listingservice.entity.Listing;
import com.marketplace.listingservice.entity.ListingStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for ListingRepository.
 * Uses H2 in-memory database for testing.
 */
@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = {"listing-created", "listing-updated", "listing-status-changed", "listing-deleted"})
@Transactional
class ListingRepositoryTest {

    @Autowired
    private ListingRepository listingRepository;

    private Listing testListing1;
    private Listing testListing2;
    private Listing testListing3;

    @BeforeEach
    void setUp() {
        listingRepository.deleteAll();

        testListing1 = Listing.builder()
                .ownerUserId(100L)
                .cardId(200L)
                .conditionRating(8)
                .listingStatus(ListingStatus.ACTIVE)
                .build();

        testListing2 = Listing.builder()
                .ownerUserId(100L)
                .cardId(201L)
                .conditionRating(7)
                .listingStatus(ListingStatus.COMPLETED)
                .build();

        testListing3 = Listing.builder()
                .ownerUserId(101L)
                .cardId(200L)
                .conditionRating(9)
                .listingStatus(ListingStatus.ACTIVE)
                .build();

        listingRepository.saveAll(List.of(testListing1, testListing2, testListing3));
    }

    @Test
    @DisplayName("Should save and retrieve listing")
    void saveAndRetrieveListing() {
        Listing newListing = Listing.builder()
                .ownerUserId(102L)
                .cardId(202L)
                .conditionRating(6)
                .listingStatus(ListingStatus.ACTIVE)
                .build();

        Listing savedListing = listingRepository.save(newListing);

        assertThat(savedListing.getListingId()).isNotNull();
        assertThat(savedListing.getOwnerUserId()).isEqualTo(102L);
        assertThat(savedListing.getCardId()).isEqualTo(202L);
    }

    @Test
    @DisplayName("Should find listings by owner user ID")
    void findByOwnerUserId() {
        List<Listing> listings = listingRepository.findByOwnerUserId(100L);

        assertThat(listings).hasSize(2);
        assertThat(listings).allMatch(l -> l.getOwnerUserId().equals(100L));
    }

    @Test
    @DisplayName("Should find listings by card ID")
    void findByCardId() {
        List<Listing> listings = listingRepository.findByCardId(200L);

        assertThat(listings).hasSize(2);
        assertThat(listings).allMatch(l -> l.getCardId().equals(200L));
    }

    @Test
    @DisplayName("Should find listings by status")
    void findByListingStatus() {
        List<Listing> activeListings = listingRepository.findByListingStatus(ListingStatus.ACTIVE);
        List<Listing> completedListings = listingRepository.findByListingStatus(ListingStatus.COMPLETED);

        assertThat(activeListings).hasSize(2);
        assertThat(completedListings).hasSize(1);
    }

    @Test
    @DisplayName("Should find listings by owner and status")
    void findByOwnerUserIdAndListingStatus() {
        List<Listing> listings = listingRepository.findByOwnerUserIdAndListingStatus(
                100L, ListingStatus.ACTIVE);

        assertThat(listings).hasSize(1);
        assertThat(listings.get(0).getOwnerUserId()).isEqualTo(100L);
        assertThat(listings.get(0).getListingStatus()).isEqualTo(ListingStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should find listings by card ID and status")
    void findByCardIdAndListingStatus() {
        List<Listing> listings = listingRepository.findByCardIdAndListingStatus(
                200L, ListingStatus.ACTIVE);

        assertThat(listings).hasSize(2);
        assertThat(listings).allMatch(l -> l.getCardId().equals(200L));
        assertThat(listings).allMatch(l -> l.getListingStatus().equals(ListingStatus.ACTIVE));
    }

    @Test
    @DisplayName("Should return empty list when no listings found")
    void findByOwnerUserId_Empty() {
        List<Listing> listings = listingRepository.findByOwnerUserId(999L);

        assertThat(listings).isEmpty();
    }

    @Test
    @DisplayName("Should delete listing")
    void deleteListing() {
        long countBefore = listingRepository.count();
        listingRepository.delete(testListing1);
        long countAfter = listingRepository.count();

        assertThat(countAfter).isEqualTo(countBefore - 1);
    }

    @Test
    @DisplayName("Should update listing")
    void updateListing() {
        testListing1.setConditionRating(5);
        Listing updated = listingRepository.save(testListing1);

        assertThat(updated.getConditionRating()).isEqualTo(5);
    }
}
