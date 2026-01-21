package com.marketplace.auth.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Unit tests for JwtUtil.
 */
class JwtUtilTest {

    private JwtUtil jwtUtil;

    private static final String SECRET_KEY = "my-super-secret-key-for-testing-purposes-256-bit";
    private static final Long EXPIRATION = 86400000L; // 24 hours

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secretKey", SECRET_KEY);
        ReflectionTestUtils.setField(jwtUtil, "expiration", EXPIRATION);
    }

    @Test
    @DisplayName("Should generate token with username only")
    void generateToken_WithUsernameOnly_ReturnsValidToken() {
        // Act
        String token = jwtUtil.generateToken("testuser");

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals("testuser", jwtUtil.extractUsername(token));
    }

    @Test
    @DisplayName("Should generate token with username and role")
    void generateToken_WithUsernameAndRole_ReturnsValidToken() {
        // Act
        String token = jwtUtil.generateToken("testuser", "USER");

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals("testuser", jwtUtil.extractUsername(token));
    }

    @Test
    @DisplayName("Should extract username from token")
    void extractUsername_ValidToken_ReturnsUsername() {
        // Arrange
        String token = jwtUtil.generateToken("johndoe");

        // Act
        String username = jwtUtil.extractUsername(token);

        // Assert
        assertEquals("johndoe", username);
    }

    @Test
    @DisplayName("Should validate token for correct username")
    void isTokenValid_CorrectUsername_ReturnsTrue() {
        // Arrange
        String token = jwtUtil.generateToken("testuser");

        // Act
        boolean isValid = jwtUtil.isTokenValid(token, "testuser");

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should invalidate token for incorrect username")
    void isTokenValid_IncorrectUsername_ReturnsFalse() {
        // Arrange
        String token = jwtUtil.generateToken("testuser");

        // Act
        boolean isValid = jwtUtil.isTokenValid(token, "wronguser");

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should return expiration time")
    void getExpiration_ReturnsConfiguredExpiration() {
        // Act
        Long expiration = jwtUtil.getExpiration();

        // Assert
        assertEquals(EXPIRATION, expiration);
    }

    @Test
    @DisplayName("Should generate unique tokens for different users")
    void generateToken_DifferentUsers_ReturnsDifferentTokens() {
        // Act
        String token1 = jwtUtil.generateToken("user1");
        String token2 = jwtUtil.generateToken("user2");

        // Assert
        assertNotEquals(token1, token2);
    }

    @Test
    @DisplayName("Should include role in generated token")
    void generateToken_WithRole_IncludesRoleInToken() {
        // Act
        String token = jwtUtil.generateToken("admin", "ADMIN");

        // Assert
        assertNotNull(token);
        assertTrue(jwtUtil.isTokenValid(token, "admin"));
    }
}
