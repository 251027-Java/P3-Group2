package com.marketplace.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model representing an application user.
 * This is a simplified model used for mocking. In production, user data
 * will be fetched from the AppUser-service via REST or messaging.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppUser {
    private Long id;
    private String email;
    private String username;
    private String passwordHash;
    private Role role;
}
