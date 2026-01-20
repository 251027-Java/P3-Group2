// Generated with Assistance By Clause Opus 4.5
// Reviewed and modified by Marcus Wright 

package com.marketplace.listingservice.dto;

import com.marketplace.listingservice.entity.ListingStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new listing.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateListingRequest {

    @NotNull(message = "Owner user ID is required")
    private Long ownerUserId;

    @NotNull(message = "Card ID is required")
    private Long cardId;

    @Min(value = 1, message = "Condition rating must be at least 1")
    @Max(value = 10, message = "Condition rating must be at most 10")
    private Integer conditionRating;

    private ListingStatus listingStatus;
}
