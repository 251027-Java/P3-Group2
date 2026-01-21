// Generated with Assistance By Clause Opus 4.5
// Reviewed and modified by Marcus Wright

package com.marketplace.listingservice.service.impl;

import com.marketplace.listingservice.client.CardServiceClient;
import com.marketplace.listingservice.client.UserServiceClient;
import com.marketplace.listingservice.client.dto.CardResponse;
import com.marketplace.listingservice.client.dto.UserResponse;
import com.marketplace.listingservice.dto.CreateListingRequest;
import com.marketplace.listingservice.dto.ListingResponse;
import com.marketplace.listingservice.dto.UpdateListingRequest;
import com.marketplace.listingservice.entity.Listing;
import com.marketplace.listingservice.entity.ListingStatus;
import com.marketplace.listingservice.exception.CardNotFoundException;
import com.marketplace.listingservice.exception.InvalidListingOperationException;
import com.marketplace.listingservice.exception.ListingNotFoundException;
import com.marketplace.listingservice.exception.UserNotFoundException;
import com.marketplace.listingservice.kafka.ListingEventProducer;
import com.marketplace.listingservice.repository.ListingRepository;
import com.marketplace.listingservice.service.ListingService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of the ListingService interface.
 * Handles all business logic for listing operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ListingServiceImpl implements ListingService {

    private final ListingRepository listingRepository;
    private final ListingEventProducer listingEventProducer;
    private final CardServiceClient cardServiceClient;
    private final UserServiceClient userServiceClient;

    @Override
    public ListingResponse createListing(CreateListingRequest request) {
        log.info("Creating new listing for card ID: {} by user ID: {}", request.getCardId(), request.getOwnerUserId());

        // Verify that the user exists via user service
        Optional<UserResponse> userResponse = userServiceClient.getUserById(request.getOwnerUserId());
        if (userResponse.isEmpty()) {
            throw new UserNotFoundException(request.getOwnerUserId());
        }

        // Verify that the card exists via card service
        Optional<CardResponse> cardResponse = cardServiceClient.getCardById(request.getCardId());
        if (cardResponse.isEmpty()) {
            throw new CardNotFoundException(request.getCardId());
        }

        Listing listing = Listing.builder()
                .ownerUserId(request.getOwnerUserId())
                .cardId(request.getCardId())
                .conditionRating(request.getConditionRating())
                .listingStatus(request.getListingStatus() != null ? request.getListingStatus() : ListingStatus.ACTIVE)
                .build();

        Listing savedListing = listingRepository.save(listing);
        log.info("Listing created successfully with ID: {}", savedListing.getListingId());

        // Publish listing created event
        listingEventProducer.sendListingCreatedEvent(savedListing);

        return ListingResponse.fromEntity(savedListing);
    }

    @Override
    @Transactional(readOnly = true)
    public ListingResponse getListingById(Long listingId) {
        log.debug("Fetching listing with ID: {}", listingId);

        Listing listing =
                listingRepository.findById(listingId).orElseThrow(() -> new ListingNotFoundException(listingId));

        return ListingResponse.fromEntity(listing);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ListingResponse> getAllListings() {
        log.debug("Fetching all listings");

        return listingRepository.findAll().stream()
                .map(ListingResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ListingResponse> getListingsByOwnerUserId(Long ownerUserId) {
        log.debug("Fetching listings for owner user ID: {}", ownerUserId);

        return listingRepository.findByOwnerUserId(ownerUserId).stream()
                .map(ListingResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ListingResponse> getListingsByCardId(Long cardId) {
        log.debug("Fetching listings for card ID: {}", cardId);

        return listingRepository.findByCardId(cardId).stream()
                .map(ListingResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ListingResponse> getListingsByStatus(ListingStatus status) {
        log.debug("Fetching listings with status: {}", status);

        return listingRepository.findByListingStatus(status).stream()
                .map(ListingResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ListingResponse> getActiveListings() {
        log.debug("Fetching all active listings");

        return listingRepository.findByListingStatus(ListingStatus.ACTIVE).stream()
                .map(ListingResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public ListingResponse updateListing(Long listingId, UpdateListingRequest request) {
        log.info("Updating listing with ID: {}", listingId);

        Listing listing =
                listingRepository.findById(listingId).orElseThrow(() -> new ListingNotFoundException(listingId));

        if (listing.getListingStatus() != ListingStatus.ACTIVE) {
            throw new InvalidListingOperationException(
                    "Cannot update a listing that is not active. Current status: " + listing.getListingStatus());
        }

        if (request.getConditionRating() != null) {
            listing.setConditionRating(request.getConditionRating());
        }

        if (request.getListingStatus() != null) {
            listing.setListingStatus(request.getListingStatus());
        }

        Listing updatedListing = listingRepository.save(listing);
        log.info("Listing updated successfully with ID: {}", updatedListing.getListingId());

        // Publish listing updated event
        listingEventProducer.sendListingUpdatedEvent(updatedListing);

        return ListingResponse.fromEntity(updatedListing);
    }

    @Override
    public ListingResponse cancelListing(Long listingId) {
        log.info("Cancelling listing with ID: {}", listingId);

        Listing listing =
                listingRepository.findById(listingId).orElseThrow(() -> new ListingNotFoundException(listingId));

        if (listing.getListingStatus() != ListingStatus.ACTIVE) {
            throw new InvalidListingOperationException(
                    "Cannot cancel a listing that is not active. Current status: " + listing.getListingStatus());
        }

        listing.setListingStatus(ListingStatus.CANCELLED);
        Listing cancelledListing = listingRepository.save(listing);
        log.info("Listing cancelled successfully with ID: {}", cancelledListing.getListingId());

        // Publish listing cancelled event
        listingEventProducer.sendListingStatusChangedEvent(cancelledListing, "CANCELLED");

        return ListingResponse.fromEntity(cancelledListing);
    }

    @Override
    public ListingResponse completeListing(Long listingId) {
        log.info("Completing listing with ID: {}", listingId);

        Listing listing =
                listingRepository.findById(listingId).orElseThrow(() -> new ListingNotFoundException(listingId));

        if (listing.getListingStatus() != ListingStatus.ACTIVE) {
            throw new InvalidListingOperationException(
                    "Cannot complete a listing that is not active. Current status: " + listing.getListingStatus());
        }

        listing.setListingStatus(ListingStatus.COMPLETED);
        Listing completedListing = listingRepository.save(listing);
        log.info("Listing completed successfully with ID: {}", completedListing.getListingId());

        // Publish listing completed event
        listingEventProducer.sendListingStatusChangedEvent(completedListing, "COMPLETED");

        return ListingResponse.fromEntity(completedListing);
    }

    @Override
    public void deleteListing(Long listingId) {
        log.info("Deleting listing with ID: {}", listingId);

        Listing listing =
                listingRepository.findById(listingId).orElseThrow(() -> new ListingNotFoundException(listingId));

        listingRepository.delete(listing);
        log.info("Listing deleted successfully with ID: {}", listingId);

        // Publish listing deleted event
        listingEventProducer.sendListingDeletedEvent(listingId);
    }

    /**
     * Validates that a user exists by calling the user service.
     *
     * @param userId the user ID to validate
     * @throws UserNotFoundException if the user does not exist
     */
    // private void validateUserExists(Long userId) {
    //     log.debug("Validating user exists with ID: {}", userId);
    //     Boolean exists;
    //     try {
    //         exists = userServiceClient.userExists(userId);
    //     } catch (FeignException e) {
    //         log.warn("User service unavailable. Cannot verify user existence for user ID: {}", userId);
    //         throw new UserNotFoundException(userId);
    //     }
    //     if (exists == null || !exists) {
    //         log.warn("User validation failed - user not found with ID: {}", userId);
    //         throw new UserNotFoundException(userId);
    //     }
    //     log.debug("User validated successfully with ID: {}", userId);
    // }

    // /**
    //  * Validates that a card exists by calling the card service.
    //  *
    //  * @param cardId the card ID to validate
    //  * @throws CardNotFoundException if the card does not exist
    //  */
    // private void validateCardExists(Long cardId) {
    //     log.debug("Validating card exists with ID: {}", cardId);
    //     CardResponse cardResponse;
    //     try {
    //         cardResponse = cardServiceClient.getCardById(cardId);
    //     } catch (FeignException e) {
    //         log.warn("Card service unavailable. Cannot verify card existence for card ID: {}", cardId);
    //         throw new CardNotFoundException(cardId);
    //     }
    //     if (cardResponse == null || cardResponse.getCardId() == null) {
    //         log.warn("Card validation failed - card not found with ID: {}", cardId);
    //         throw new CardNotFoundException(cardId);
    //     }
    //     log.debug("Card validated successfully with ID: {}", cardId);
    // }
}
