// Generated with Assistance By Clause Opus 4.5
// Reviewed and modified by Marcus Wright 

package com.marketplace.listingservice.dto;

import com.marketplace.listingservice.entity.Listing;
import com.marketplace.listingservice.entity.ListingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for listing responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListingResponse {

    private Long listingId;
    private Long ownerUserId;
    private Long cardId;
    private Integer conditionRating;
    private ListingStatus listingStatus;
    private LocalDateTime createdAt;

    /**
     * Converts a Listing entity to a ListingResponse DTO.
     *
     * @param listing the listing entity
     * @return the listing response DTO
     */
    public static ListingResponse fromEntity(Listing listing) {
        return ListingResponse.builder()
                .listingId(listing.getListingId())
                .ownerUserId(listing.getOwnerUserId())
                .cardId(listing.getCardId())
                .conditionRating(listing.getConditionRating())
                .listingStatus(listing.getListingStatus())
                .createdAt(listing.getCreatedAt())
                .build();
    }
}
