// Generated with assistance from Antigravity through Gemini
// Reviewed and modified by Liam Ruiz
package com.marketplace.auth.service;

import com.marketplace.auth.client.UserServiceClient;
import com.marketplace.auth.client.dto.AuthUserResponse;
import com.marketplace.auth.client.dto.CreateUserRequest;
import com.marketplace.auth.client.dto.UserResponse;
import com.marketplace.auth.dto.AuthResponse;
import com.marketplace.auth.dto.LoginRequest;
import com.marketplace.auth.dto.RegisterRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service for handling user authentication.
 * Uses Feign client to communicate with user-service for user management.
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserServiceClient userServiceClient;

    public AuthService(JwtUtil jwtUtil, PasswordEncoder passwordEncoder, UserServiceClient userServiceClient) {
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userServiceClient = userServiceClient;
    }

    /**
     * Registers a new user via user-service.
     *
     * @param request The registration request.
     * @return AuthResponse containing the JWT token.
     * @throws IllegalArgumentException if registration fails.
     */
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering user: {}", request.getEmail());

        // Create user via user-service
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(request.getPassword())
                .role("USER")
                .build();

        UserResponse createdUser = userServiceClient.createUser(createUserRequest);

        if (createdUser == null) {
            log.error("Failed to create user: {}", request.getEmail());
            throw new IllegalArgumentException("Failed to create user. Email may already be registered.");
        }

        log.info("User registered successfully: {}", createdUser.getUsername());

        String token = jwtUtil.generateToken(createdUser.getUsername(), createdUser.getRole());

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .expiresIn(jwtUtil.getExpiration())
                .username(createdUser.getUsername())
                .role(createdUser.getRole())
                .build();
    }

    /**
     * Authenticates a user and returns a JWT token.
     *
     * @param request The login request.
     * @return AuthResponse containing the JWT token.
     * @throws IllegalArgumentException if credentials are invalid.
     */
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for: {}", request.getEmail());

        // Get user from user-service (including password hash)
        AuthUserResponse user = userServiceClient.getUserForAuth(request.getEmail());

        if (user == null) {
            log.warn("User not found or service unavailable: {}", request.getEmail());
            throw new IllegalArgumentException("Invalid email or password");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            log.warn("Invalid password for user: {}", request.getEmail());
            throw new IllegalArgumentException("Invalid email or password");
        }

        log.info("User authenticated: {} (role: {})", user.getUsername(), user.getRole());
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .expiresIn(jwtUtil.getExpiration())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }
}
