package com.marketplace.listingservice.service.impl;

import com.marketplace.listingservice.dto.CreateListingRequest;
import com.marketplace.listingservice.dto.ListingResponse;
import com.marketplace.listingservice.dto.UpdateListingRequest;
import com.marketplace.listingservice.entity.Listing;
import com.marketplace.listingservice.entity.ListingStatus;
import com.marketplace.listingservice.exception.InvalidListingOperationException;
import com.marketplace.listingservice.exception.ListingNotFoundException;
import com.marketplace.listingservice.kafka.ListingEventProducer;
import com.marketplace.listingservice.repository.ListingRepository;
import com.marketplace.listingservice.service.ListingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    @Override
    public ListingResponse createListing(CreateListingRequest request) {
        log.info("Creating new listing for card ID: {} by user ID: {}", 
                request.getCardId(), request.getOwnerUserId());

        Listing listing = Listing.builder()
                .ownerUserId(request.getOwnerUserId())
                .cardId(request.getCardId())
                .conditionRating(request.getConditionRating())
                .listingStatus(request.getListingStatus() != null ? 
                        request.getListingStatus() : ListingStatus.ACTIVE)
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
        
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException(listingId));
        
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

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException(listingId));

        if (listing.getListingStatus() != ListingStatus.ACTIVE) {
            throw new InvalidListingOperationException(
                    "Cannot update a listing that is not active. Current status: " + 
                    listing.getListingStatus());
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

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException(listingId));

        if (listing.getListingStatus() != ListingStatus.ACTIVE) {
            throw new InvalidListingOperationException(
                    "Cannot cancel a listing that is not active. Current status: " + 
                    listing.getListingStatus());
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

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException(listingId));

        if (listing.getListingStatus() != ListingStatus.ACTIVE) {
            throw new InvalidListingOperationException(
                    "Cannot complete a listing that is not active. Current status: " + 
                    listing.getListingStatus());
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

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException(listingId));

        listingRepository.delete(listing);
        log.info("Listing deleted successfully with ID: {}", listingId);

        // Publish listing deleted event
        listingEventProducer.sendListingDeletedEvent(listingId);
    }
}
