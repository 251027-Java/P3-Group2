package com.marketplace.auth.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for internal authentication responses from user-service.
 * Includes password hash for credential verification.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthUserResponse {
    private Long userId;
    private String email;
    private String username;
    private String passwordHash;
    private String role;
}
