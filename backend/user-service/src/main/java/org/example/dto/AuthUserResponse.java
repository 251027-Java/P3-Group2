package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.Model.User;

/**
 * DTO for internal authentication responses.
 * Includes password hash for auth-service to verify credentials.
 * This endpoint should only be called by internal services.
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

    public static AuthUserResponse fromUser(User user) {
        return AuthUserResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .username(user.getUsername())
                .passwordHash(user.getPasswordHash())
                .role(user.getRole())
                .build();
    }
}
