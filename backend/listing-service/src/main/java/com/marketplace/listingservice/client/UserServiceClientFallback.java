// Generated with Assistance By Clause Opus 4.5
// Reviewed and modified by Marcus Wright

package com.marketplace.listingservice.client;

import com.marketplace.listingservice.client.dto.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Fallback implementation for UserServiceClient.
 * Used when the user service is unavailable.
 */
@Component
@Slf4j
public class UserServiceClientFallback implements UserServiceClient {

    @Override
    public Optional<UserResponse> getUserById(Long userId) {
        log.warn("User service unavailable. Returning fallback response for user ID: {}", userId);
        return Optional.empty();
    }
}
