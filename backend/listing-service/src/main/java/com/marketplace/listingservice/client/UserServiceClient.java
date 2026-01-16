package com.marketplace.listingservice.client;

import com.marketplace.listingservice.client.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
    UserResponse getUserById(@PathVariable("userId") Long userId);

    /**
     * Checks if a user exists by user ID.
     *
     * @param userId the user ID
     * @return true if the user exists, false otherwise
     */
    @GetMapping("/api/users/{userId}/exists")
    Boolean userExists(@PathVariable("userId") Long userId);
}
