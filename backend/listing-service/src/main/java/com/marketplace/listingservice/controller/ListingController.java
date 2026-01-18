// Generated with Assistance By Clause Opus 4.5
// Reviewed and modified by Marcus Wright

package com.marketplace.listingservice.controller;

import com.marketplace.listingservice.dto.CreateListingRequest;
import com.marketplace.listingservice.dto.ListingResponse;
import com.marketplace.listingservice.dto.UpdateListingRequest;
import com.marketplace.listingservice.entity.ListingStatus;
import com.marketplace.listingservice.service.ListingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for listing operations.
 * Provides endpoints for CRUD operations on card trade listings.
 */
@RestController
@RequestMapping("/api/listings")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Listing Controller", description = "API endpoints for managing card trade listings")
public class ListingController {

    private final ListingService listingService;

    @PostMapping
    @Operation(summary = "Create a new listing", description = "Creates a new card trade listing")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Listing created successfully",
                        content = @Content(schema = @Schema(implementation = ListingResponse.class))),
                @ApiResponse(responseCode = "400", description = "Invalid request data"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<ListingResponse> createListing(@Valid @RequestBody CreateListingRequest request) {
        log.info("Received request to create listing for card ID: {}", request.getCardId());
        ListingResponse response = listingService.createListing(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{listingId}")
    @Operation(summary = "Get listing by ID", description = "Retrieves a listing by its unique identifier")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Listing found",
                        content = @Content(schema = @Schema(implementation = ListingResponse.class))),
                @ApiResponse(responseCode = "404", description = "Listing not found")
            })
    public ResponseEntity<ListingResponse> getListingById(
            @Parameter(description = "Listing ID") @PathVariable Long listingId) {
        log.info("Received request to get listing with ID: {}", listingId);
        ListingResponse response = listingService.getListingById(listingId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all listings", description = "Retrieves all card trade listings")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Listings retrieved successfully")})
    public ResponseEntity<List<ListingResponse>> getAllListings() {
        log.info("Received request to get all listings");
        List<ListingResponse> listings = listingService.getAllListings();
        return ResponseEntity.ok(listings);
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active listings", description = "Retrieves all active card trade listings")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Active listings retrieved successfully")})
    public ResponseEntity<List<ListingResponse>> getActiveListings() {
        log.info("Received request to get all active listings");
        List<ListingResponse> listings = listingService.getActiveListings();
        return ResponseEntity.ok(listings);
    }

    @GetMapping("/user/{ownerUserId}")
    @Operation(summary = "Get listings by owner", description = "Retrieves all listings owned by a specific user")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Listings retrieved successfully")})
    public ResponseEntity<List<ListingResponse>> getListingsByOwnerUserId(
            @Parameter(description = "Owner User ID") @PathVariable Long ownerUserId) {
        log.info("Received request to get listings for owner user ID: {}", ownerUserId);
        List<ListingResponse> listings = listingService.getListingsByOwnerUserId(ownerUserId);
        return ResponseEntity.ok(listings);
    }

    @GetMapping("/card/{cardId}")
    @Operation(summary = "Get listings by card", description = "Retrieves all listings for a specific card")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Listings retrieved successfully")})
    public ResponseEntity<List<ListingResponse>> getListingsByCardId(
            @Parameter(description = "Card ID") @PathVariable Long cardId) {
        log.info("Received request to get listings for card ID: {}", cardId);
        List<ListingResponse> listings = listingService.getListingsByCardId(cardId);
        return ResponseEntity.ok(listings);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get listings by status", description = "Retrieves all listings with a specific status")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Listings retrieved successfully"),
                @ApiResponse(responseCode = "400", description = "Invalid status value")
            })
    public ResponseEntity<List<ListingResponse>> getListingsByStatus(
            @Parameter(description = "Listing Status") @PathVariable ListingStatus status) {
        log.info("Received request to get listings with status: {}", status);
        List<ListingResponse> listings = listingService.getListingsByStatus(status);
        return ResponseEntity.ok(listings);
    }

    @PutMapping("/{listingId}")
    @Operation(summary = "Update a listing", description = "Updates an existing listing")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Listing updated successfully",
                        content = @Content(schema = @Schema(implementation = ListingResponse.class))),
                @ApiResponse(responseCode = "400", description = "Invalid request data or operation not allowed"),
                @ApiResponse(responseCode = "404", description = "Listing not found")
            })
    public ResponseEntity<ListingResponse> updateListing(
            @Parameter(description = "Listing ID") @PathVariable Long listingId,
            @Valid @RequestBody UpdateListingRequest request) {
        log.info("Received request to update listing with ID: {}", listingId);
        ListingResponse response = listingService.updateListing(listingId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{listingId}/cancel")
    @Operation(summary = "Cancel a listing", description = "Cancels an active listing")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Listing cancelled successfully",
                        content = @Content(schema = @Schema(implementation = ListingResponse.class))),
                @ApiResponse(responseCode = "400", description = "Operation not allowed"),
                @ApiResponse(responseCode = "404", description = "Listing not found")
            })
    public ResponseEntity<ListingResponse> cancelListing(
            @Parameter(description = "Listing ID") @PathVariable Long listingId) {
        log.info("Received request to cancel listing with ID: {}", listingId);
        ListingResponse response = listingService.cancelListing(listingId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{listingId}/complete")
    @Operation(summary = "Complete a listing", description = "Marks a listing as completed")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Listing completed successfully",
                        content = @Content(schema = @Schema(implementation = ListingResponse.class))),
                @ApiResponse(responseCode = "400", description = "Operation not allowed"),
                @ApiResponse(responseCode = "404", description = "Listing not found")
            })
    public ResponseEntity<ListingResponse> completeListing(
            @Parameter(description = "Listing ID") @PathVariable Long listingId) {
        log.info("Received request to complete listing with ID: {}", listingId);
        ListingResponse response = listingService.completeListing(listingId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{listingId}")
    @Operation(summary = "Delete a listing", description = "Permanently deletes a listing")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "204", description = "Listing deleted successfully"),
                @ApiResponse(responseCode = "404", description = "Listing not found")
            })
    public ResponseEntity<Void> deleteListing(@Parameter(description = "Listing ID") @PathVariable Long listingId) {
        log.info("Received request to delete listing with ID: {}", listingId);
        listingService.deleteListing(listingId);
        return ResponseEntity.noContent().build();
    }
}
