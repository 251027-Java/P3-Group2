// Generated with Assistance By Clause Opus 4.5
// Reviewed and modified by Marcus Wright

package com.marketplace.listingservice.service;

import com.marketplace.listingservice.dto.CreateListingRequest;
import com.marketplace.listingservice.dto.ListingResponse;
import com.marketplace.listingservice.dto.UpdateListingRequest;
import com.marketplace.listingservice.entity.ListingStatus;

import java.util.List;

/**
 * Service interface for listing operations.
 */
public interface ListingService {

    /**
     * Create a new listing.
     *
     * @param request the create listing request
     * @return the created listing response
     */
    ListingResponse createListing(CreateListingRequest request);

    /**
     * Get a listing by ID.
     *
     * @param listingId the listing ID
     * @return the listing response
     */
    ListingResponse getListingById(Long listingId);

    /**
     * Get all listings.
     *
     * @return list of all listing responses
     */
    List<ListingResponse> getAllListings();

    /**
     * Get all listings by owner user ID.
     *
     * @param ownerUserId the owner user ID
     * @return list of listing responses for the owner
     */
    List<ListingResponse> getListingsByOwnerUserId(Long ownerUserId);

    /**
     * Get all listings by card ID.
     *
     * @param cardId the card ID
     * @return list of listing responses for the card
     */
    List<ListingResponse> getListingsByCardId(Long cardId);

    /**
     * Get all listings by status.
     *
     * @param status the listing status
     * @return list of listing responses with the status
     */
    List<ListingResponse> getListingsByStatus(ListingStatus status);

    /**
     * Get all active listings.
     *
     * @return list of active listing responses
     */
    List<ListingResponse> getActiveListings();

    /**
     * Update a listing.
     *
     * @param listingId the listing ID
     * @param request the update listing request
     * @return the updated listing response
     */
    ListingResponse updateListing(Long listingId, UpdateListingRequest request);

    /**
     * Cancel a listing.
     *
     * @param listingId the listing ID
     * @return the cancelled listing response
     */
    ListingResponse cancelListing(Long listingId);

    /**
     * Complete a listing.
     *
     * @param listingId the listing ID
     * @return the completed listing response
     */
    ListingResponse completeListing(Long listingId);

    /**
     * Delete a listing.
     *
     * @param listingId the listing ID
     */
    void deleteListing(Long listingId);
}
