package main.java.com.marketplace.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for authentication responses containing the JWT token.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String type;
    private Long expiresIn;
    private String username;
    private String role;
}
