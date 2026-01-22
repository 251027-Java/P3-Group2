package com.marketplace.auth.client;

import com.marketplace.auth.client.dto.AuthUserResponse;
import com.marketplace.auth.client.dto.CreateUserRequest;
import com.marketplace.auth.client.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

/**
 * Feign client for communicating with the User Service.
 * Allows auth-service to create and retrieve users.
 */
@FeignClient(name = "user-service", fallback = UserServiceClientFallback.class)
public interface UserServiceClient {

    /**
     * Retrieves user with password hash for authentication.
     * Uses internal endpoint that includes credential data.
     *
     * @param email the user's email
     * @return the user details including password hash
     */
    @GetMapping("/api/users/internal/email/{email}")
    Optional<AuthUserResponse> getUserForAuth(@PathVariable("email") String email);

    /**
     * Creates a new user.
     *
     * @param request the user creation request
     * @return the created user details
     */
    @PostMapping("/api/users")
    UserResponse createUser(@RequestBody CreateUserRequest request);
}
