package com.marketplace.auth.service;

import com.marketplace.auth.dto.AuthResponse;
import com.marketplace.auth.dto.LoginRequest;
import com.marketplace.auth.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthService.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthService authService;

    private static final String TEST_TOKEN = "test.jwt.token";
    private static final Long EXPIRATION = 86400000L;

    @BeforeEach
    void setUp() {
        // Setup mock behavior for password encoder
        when(passwordEncoder.encode(anyString())).thenAnswer(invocation -> "encoded_" + invocation.getArgument(0));
        
        // Setup mock behavior for JwtUtil
        lenient().when(jwtUtil.generateToken(anyString(), anyString())).thenReturn(TEST_TOKEN);
        lenient().when(jwtUtil.getExpiration()).thenReturn(EXPIRATION);

        authService = new AuthService(jwtUtil, passwordEncoder);
    }

    @Test
    @DisplayName("Should register new user successfully")
    void register_NewUser_ReturnsAuthResponseWithToken() {
        // Arrange
        RegisterRequest request = new RegisterRequest("newuser@example.com", "newuser", "password123");

        // Act
        AuthResponse response = authService.register(request);

        // Assert
        assertNotNull(response);
        assertEquals(TEST_TOKEN, response.getToken());
        assertEquals("Bearer", response.getType());
        assertEquals("newuser", response.getUsername());
        assertEquals("USER", response.getRole());
        assertEquals(EXPIRATION, response.getExpiresIn());
        
        verify(passwordEncoder).encode("password123");
        verify(jwtUtil).generateToken("newuser", "USER");
    }

    @Test
    @DisplayName("Should throw exception when registering with existing email")
    void register_ExistingEmail_ThrowsIllegalArgumentException() {
        // Arrange - admin@example.com is seeded in the service
        RegisterRequest request = new RegisterRequest("admin@example.com", "newadmin", "password");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> authService.register(request));
        assertEquals("Email already registered", exception.getMessage());
    }

    @Test
    @DisplayName("Should login seeded admin user successfully")
    void login_ValidAdminCredentials_ReturnsAuthResponseWithToken() {
        // Arrange
        when(passwordEncoder.matches("admin123", "encoded_admin123")).thenReturn(true);
        LoginRequest request = new LoginRequest("admin@example.com", "admin123");

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals(TEST_TOKEN, response.getToken());
        assertEquals("Bearer", response.getType());
        assertEquals("admin", response.getUsername());
        assertEquals("ADMIN", response.getRole());
        assertEquals(EXPIRATION, response.getExpiresIn());
    }

    @Test
    @DisplayName("Should login seeded regular user successfully")
    void login_ValidUserCredentials_ReturnsAuthResponseWithToken() {
        // Arrange
        when(passwordEncoder.matches("user123", "encoded_user123")).thenReturn(true);
        LoginRequest request = new LoginRequest("user@example.com", "user123");

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals(TEST_TOKEN, response.getToken());
        assertEquals("Bearer", response.getType());
        assertEquals("user", response.getUsername());
        assertEquals("USER", response.getRole());
    }

    @Test
    @DisplayName("Should throw exception for non-existent email on login")
    void login_NonExistentEmail_ThrowsIllegalArgumentException() {
        // Arrange
        LoginRequest request = new LoginRequest("nonexistent@example.com", "password");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.login(request));
        assertEquals("Invalid email or password", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for wrong password on login")
    void login_WrongPassword_ThrowsIllegalArgumentException() {
        // Arrange
        when(passwordEncoder.matches("wrongpassword", "encoded_admin123")).thenReturn(false);
        LoginRequest request = new LoginRequest("admin@example.com", "wrongpassword");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.login(request));
        assertEquals("Invalid email or password", exception.getMessage());
    }

    @Test
    @DisplayName("Should register and then login with new user")
    void registerAndLogin_NewUser_WorksSuccessfully() {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest("test@example.com", "testuser", "testpass");
        
        // Act - Register
        AuthResponse registerResponse = authService.register(registerRequest);
        
        // Assert registration
        assertNotNull(registerResponse);
        assertEquals("testuser", registerResponse.getUsername());

        // Arrange - Login
        when(passwordEncoder.matches("testpass", "encoded_testpass")).thenReturn(true);
        LoginRequest loginRequest = new LoginRequest("test@example.com", "testpass");
        
        // Act - Login
        AuthResponse loginResponse = authService.login(loginRequest);

        // Assert login
        assertNotNull(loginResponse);
        assertEquals("testuser", loginResponse.getUsername());
        assertEquals("USER", loginResponse.getRole());
    }

    @Test
    @DisplayName("Should assign USER role to newly registered users")
    void register_NewUser_AssignsUserRole() {
        // Arrange
        RegisterRequest request = new RegisterRequest("regular@example.com", "regular", "password");

        // Act
        AuthResponse response = authService.register(request);

        // Assert
        assertEquals("USER", response.getRole());
        verify(jwtUtil).generateToken("regular", "USER");
    }
}
