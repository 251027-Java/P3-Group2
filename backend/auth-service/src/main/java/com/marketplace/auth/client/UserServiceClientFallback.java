package com.marketplace.auth.client;

import com.marketplace.auth.client.dto.AuthUserResponse;
import com.marketplace.auth.client.dto.CreateUserRequest;
import com.marketplace.auth.client.dto.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Fallback implementation for UserServiceClient.
 * Used when the user-service is unavailable.
 */
@Component
public class UserServiceClientFallback implements UserServiceClient {

    private static final Logger log = LoggerFactory.getLogger(UserServiceClientFallback.class);

    @Override
    public AuthUserResponse getUserForAuth(String email) {
        log.warn("User service unavailable. Cannot retrieve user for auth: {}", email);
        return null;
    }

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        log.error("User service unavailable. Cannot create user: {}", request.getEmail());
        return null;
    }
}
