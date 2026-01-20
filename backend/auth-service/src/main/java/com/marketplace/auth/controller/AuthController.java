// Generated with assistance from Antigravity through Gemini
// Reviewed and modified by Liam Ruiz
package com.marketplace.auth.controller;

import com.marketplace.auth.dto.AuthResponse;
import com.marketplace.auth.dto.LoginRequest;
import com.marketplace.auth.dto.RegisterRequest;
import com.marketplace.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for authentication endpoints.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "API for user authentication")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Registers a new user and returns a JWT token.
     *
     * @param request Registration details.
     * @return AuthResponse with JWT token.
     */
    @PostMapping("/register")
    @Operation(summary = "Register", description = "Register a new user and receive a JWT token")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Authenticates a user and returns a JWT token.
     *
     * @param request Login credentials.
     * @return AuthResponse with JWT token.
     */
    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate with email and password to receive a JWT token")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
