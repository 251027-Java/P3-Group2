// Generated with assistance from Cursor
// Reviewed and modified by Matt Selle
package com.marketplace.trade.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO for creating a trade request
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TradeRequestDTO {

    @NotNull(message = "Listing ID is required")
    private Long listingId;

    @NotNull(message = "Requesting user ID is required")
    private Long requestingUserId;

    @NotEmpty(message = "At least one card must be offered")
    private List<Long> offeredCardIds;
}
