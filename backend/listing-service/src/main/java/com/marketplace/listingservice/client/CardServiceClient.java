// Generated with Assistance By Clause Opus 4.5
// Reviewed and modified by Marcus Wright 

package com.marketplace.listingservice.client;

import com.marketplace.listingservice.client.dto.CardResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for communicating with the Card Service.
 * Allows the listing service to validate card existence and retrieve card details.
 */
@FeignClient(name = "card-service", fallback = CardServiceClientFallback.class)
public interface CardServiceClient {

    /**
     * Retrieves card details by card ID.
     *
     * @param cardId the card ID
     * @return the card details
     */
    @GetMapping("/api/cards/{cardId}")
    CardResponse getCardById(@PathVariable("cardId") Long cardId);
}
