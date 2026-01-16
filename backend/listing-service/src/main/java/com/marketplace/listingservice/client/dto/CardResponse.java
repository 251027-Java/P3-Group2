package com.marketplace.listingservice.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a card response from the Card Service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardResponse {

    private Long cardId;
    private String name;
    private String description;
    private String rarity;
    private String imageUrl;
    private String cardType;
    private Double marketValue;
}
