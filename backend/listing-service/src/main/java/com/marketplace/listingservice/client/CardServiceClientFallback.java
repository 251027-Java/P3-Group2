// Generated with Assistance By Clause Opus 4.5
// Reviewed and modified by Marcus Wright

package com.marketplace.listingservice.client;

import com.marketplace.listingservice.client.dto.CardResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Fallback implementation for CardServiceClient.
 * Used when the card service is unavailable.
 */
@Component
@Slf4j
public class CardServiceClientFallback implements CardServiceClient {

    @Override
    public Optional<CardResponse> getCardById(Long cardId) {
        log.warn("Card service unavailable. Returning fallback response for card ID: {}", cardId);
        return Optional.empty();
    }
}
