package com.marketplace.auth.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for user data returned from user-service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long userId;
    private String email;
    private String username;
    private Double latitude;
    private Double longitude;
    private String role;
    private LocalDateTime createdAt;
    private String passwordHash;
}
