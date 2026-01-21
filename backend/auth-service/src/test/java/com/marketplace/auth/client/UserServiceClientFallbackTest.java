package com.marketplace.auth.client;

import static org.junit.jupiter.api.Assertions.*;

import com.marketplace.auth.client.dto.CreateUserRequest;
import com.marketplace.auth.client.dto.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for UserServiceClientFallback.
 * Verifies fallback behavior when user-service is unavailable.
 */
class UserServiceClientFallbackTest {

    private UserServiceClientFallback fallback;

    @BeforeEach
    void setUp() {
        fallback = new UserServiceClientFallback();
    }

    @Test
    @DisplayName("getUserForAuth should return null when service unavailable")
    void getUserForAuth_ServiceUnavailable_ReturnsNull() {
        // Act
        var result = fallback.getUserForAuth("test@example.com");

        // Assert empty optional
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getUserForAuth should handle any email gracefully")
    void getUserForAuth_AnyEmail_ReturnsNull() {
        // Act & Assert
        assertNull(fallback.getUserForAuth("user1@example.com"));
        assertNull(fallback.getUserForAuth("admin@test.com"));
        assertNull(fallback.getUserForAuth(""));
        assertNull(fallback.getUserForAuth(null));
    }

    @Test
    @DisplayName("createUser should return null when service unavailable")
    void createUser_ServiceUnavailable_ReturnsNull() {
        // Arrange
        CreateUserRequest request = CreateUserRequest.builder()
                .email("newuser@example.com")
                .username("newuser")
                .passwordHash("password123")
                .role("USER")
                .build();

        // Act
        UserResponse result = fallback.createUser(request);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("createUser should handle any request gracefully")
    void createUser_AnyRequest_ReturnsNull() {
        // Arrange
        CreateUserRequest userRequest = CreateUserRequest.builder()
                .email("user@example.com")
                .username("user")
                .passwordHash("pass")
                .role("USER")
                .build();

        CreateUserRequest adminRequest = CreateUserRequest.builder()
                .email("admin@example.com")
                .username("admin")
                .passwordHash("adminpass")
                .role("ADMIN")
                .build();

        // Act & Assert
        assertNull(fallback.createUser(userRequest));
        assertNull(fallback.createUser(adminRequest));
    }
}
