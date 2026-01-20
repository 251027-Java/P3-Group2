// Generated with assistance from Antigravity through Gemini
// Reviewed and modified by Liam Ruiz
package com.marketplace.auth.service;

import com.marketplace.auth.dto.AuthResponse;
import com.marketplace.auth.dto.LoginRequest;
import com.marketplace.auth.dto.RegisterRequest;
import com.marketplace.auth.model.AppUser;
import com.marketplace.auth.model.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service for handling user authentication.
 * Currently uses an in-memory mock user store. In production, this will
 * delegate to the AppUser-service via REST or messaging.
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    // TODO: Replace with AppUser-service feign client
    private final Map<String, AppUser> usersByEmail = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public AuthService(JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        
        // Seed sample users
        seedUsers();
    }

    private void seedUsers() {
        // Admin user
        AppUser adminUser = AppUser.builder()
                .id(idGenerator.getAndIncrement())
                .email("admin@example.com")
                .username("admin")
                .passwordHash(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .build();
        usersByEmail.put(adminUser.getEmail(), adminUser);
        log.info("Seeded admin user: {} (role: {})", adminUser.getEmail(), adminUser.getRole());

        // Regular user
        AppUser regularUser = AppUser.builder()
                .id(idGenerator.getAndIncrement())
                .email("user@example.com")
                .username("user")
                .passwordHash(passwordEncoder.encode("user123"))
                .role(Role.USER)
                .build();
        usersByEmail.put(regularUser.getEmail(), regularUser);
        log.info("Seeded regular user: {} (role: {})", regularUser.getEmail(), regularUser.getRole());
    }

    /**
     * Registers a new user.
     *
     * @param request The registration request.
     * @return AuthResponse containing the JWT token.
     * @throws IllegalArgumentException if email is already registered.
     */
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering user: {}", request.getEmail());

        if (usersByEmail.containsKey(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        AppUser user = AppUser.builder()
                .id(idGenerator.getAndIncrement())
                .email(request.getEmail())
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER) // New users default to USER role
                .build();

        usersByEmail.put(user.getEmail(), user);
        log.info("User registered successfully: {}", user.getUsername());

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .expiresIn(jwtUtil.getExpiration())
                .username(user.getUsername())
                .role(user.getRole().name())
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

        Optional<AppUser> userOpt = Optional.ofNullable(usersByEmail.get(request.getEmail()));

        AppUser user = userOpt.orElseThrow(() -> 
                new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        log.info("User authenticated: {} (role: {})", user.getUsername(), user.getRole());
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());




        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .expiresIn(jwtUtil.getExpiration())
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }
}
