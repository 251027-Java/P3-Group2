package com.marketplace.listingservice.repository;

import com.marketplace.listingservice.entity.Listing;
import com.marketplace.listingservice.entity.ListingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Listing entity operations.
 */
@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {

    /**
     * Find all listings by owner user ID.
     *
     * @param ownerUserId the owner user ID
     * @return list of listings owned by the user
     */
    List<Listing> findByOwnerUserId(Long ownerUserId);

    /**
     * Find all listings by card ID.
     *
     * @param cardId the card ID
     * @return list of listings for the specified card
     */
    List<Listing> findByCardId(Long cardId);

    /**
     * Find all listings by status.
     *
     * @param listingStatus the listing status
     * @return list of listings with the specified status
     */
    List<Listing> findByListingStatus(ListingStatus listingStatus);

    /**
     * Find all listings by owner user ID and status.
     *
     * @param ownerUserId the owner user ID
     * @param listingStatus the listing status
     * @return list of listings matching the criteria
     */
    List<Listing> findByOwnerUserIdAndListingStatus(Long ownerUserId, ListingStatus listingStatus);

    /**
     * Find all active listings for a specific card.
     *
     * @param cardId the card ID
     * @param listingStatus the listing status
     * @return list of listings matching the criteria
     */
    List<Listing> findByCardIdAndListingStatus(Long cardId, ListingStatus listingStatus);
}
