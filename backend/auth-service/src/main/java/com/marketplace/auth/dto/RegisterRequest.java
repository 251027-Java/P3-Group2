package main.java.com.marketplace.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user registration requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String email;
    private String username;
    private String password;
}
