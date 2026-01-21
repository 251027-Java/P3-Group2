// Generated with Assistance By Clause Opus 4.5
// Reviewed and modified by Marcus Wright

package com.marketplace.listingservice.client;

import com.marketplace.listingservice.client.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

/**
 * Feign client for communicating with the User Service.
 * Allows the listing service to validate user existence and retrieve user details.
 */
@FeignClient(name = "user-service", fallback = UserServiceClientFallback.class)
public interface UserServiceClient {

    /**
     * Retrieves user details by user ID.
     *
     * @param userId the user ID
     * @return the user details
     */
    @GetMapping("/api/users/{userId}")
    Optional<UserResponse> getUserById(@PathVariable("userId") Long userId);
}
