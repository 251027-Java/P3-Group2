package com.marketplace.trade.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListingResponse {
    private Long listingId;
    private Long ownerUserId;
    private Long cardId;
    private Integer conditionRating;
    private ListingStatus listingStatus;
    private LocalDateTime createdAt;
}
