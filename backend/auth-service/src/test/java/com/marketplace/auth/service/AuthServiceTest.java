package com.marketplace.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.marketplace.auth.client.UserServiceClient;
import com.marketplace.auth.client.dto.AuthUserResponse;
import com.marketplace.auth.client.dto.CreateUserRequest;
import com.marketplace.auth.client.dto.UserResponse;
import com.marketplace.auth.dto.AuthResponse;
import com.marketplace.auth.dto.LoginRequest;
import com.marketplace.auth.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

/**
 * Unit tests for AuthService with UserServiceClient integration.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private AuthService authService;

    private static final String TEST_TOKEN = "test.jwt.token";
    private static final Long EXPIRATION = 86400000L;

    @BeforeEach
    void setUp() {
        lenient().when(jwtUtil.generateToken(anyString(), anyString())).thenReturn(TEST_TOKEN);
        lenient().when(jwtUtil.getExpiration()).thenReturn(EXPIRATION);
    }

    @Nested
    @DisplayName("Registration Tests")
    class RegistrationTests {

        @Test
        @DisplayName("Should register new user successfully")
        void register_NewUser_ReturnsAuthResponseWithToken() {
            // Arrange
            RegisterRequest request = new RegisterRequest("newuser@example.com", "newuser", "password123");
            UserResponse createdUser = UserResponse.builder()
                    .userId(1L)
                    .email("newuser@example.com")
                    .username("newuser")
                    .role("USER")
                    .build();

            when(userServiceClient.createUser(any(CreateUserRequest.class))).thenReturn(createdUser);

            // Act
            AuthResponse response = authService.register(request);

            // Assert
            assertNotNull(response);
            assertEquals(TEST_TOKEN, response.getToken());
            assertEquals("Bearer", response.getType());
            assertEquals("newuser", response.getUsername());
            assertEquals("USER", response.getRole());
            assertEquals(EXPIRATION, response.getExpiresIn());

            verify(userServiceClient).createUser(any(CreateUserRequest.class));
            verify(jwtUtil).generateToken("newuser", "USER");
        }

        @Test
        @DisplayName("Should throw exception when user creation fails")
        void register_UserCreationFails_ThrowsIllegalArgumentException() {
            // Arrange
            RegisterRequest request = new RegisterRequest("existing@example.com", "existing", "password");
            when(userServiceClient.createUser(any(CreateUserRequest.class))).thenReturn(null);

            // Act & Assert
            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class, () -> authService.register(request));
            assertTrue(exception.getMessage().contains("Failed to create user"));

            verify(userServiceClient).createUser(any(CreateUserRequest.class));
            verify(jwtUtil, never()).generateToken(anyString(), anyString());
        }

        @Test
        @DisplayName("Should pass correct data to user service")
        void register_CorrectDataPassedToUserService() {
            // Arrange
            RegisterRequest request = new RegisterRequest("test@example.com", "testuser", "testpass");
            UserResponse createdUser = UserResponse.builder()
                    .userId(1L)
                    .email("test@example.com")
                    .username("testuser")
                    .role("USER")
                    .build();

            when(userServiceClient.createUser(any(CreateUserRequest.class))).thenAnswer(invocation -> {
                CreateUserRequest createRequest = invocation.getArgument(0);
                assertEquals("test@example.com", createRequest.getEmail());
                assertEquals("testuser", createRequest.getUsername());
                // assertEquals("testpass", createRequest.getPasswordHash());
                assertEquals("USER", createRequest.getRole());
                return createdUser;
            });

            // Act
            authService.register(request);

            // Assert
            verify(userServiceClient).createUser(any(CreateUserRequest.class));
        }
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("Should login user successfully with valid credentials")
        void login_ValidCredentials_ReturnsAuthResponseWithToken() {
            // Arrange
            LoginRequest request = new LoginRequest("user@example.com", "password123");
            AuthUserResponse authUser = AuthUserResponse.builder()
                    .userId(1L)
                    .email("user@example.com")
                    .username("testuser")
                    .passwordHash("hashed_password")
                    .role("USER")
                    .build();

            when(userServiceClient.getUserForAuth("user@example.com")).thenReturn(Optional.of(authUser));
            when(passwordEncoder.matches("password123", "hashed_password")).thenReturn(true);

            // Act
            AuthResponse response = authService.login(request);

            // Assert
            assertNotNull(response);
            assertEquals(TEST_TOKEN, response.getToken());
            assertEquals("Bearer", response.getType());
            assertEquals("testuser", response.getUsername());
            assertEquals("USER", response.getRole());
            assertEquals(EXPIRATION, response.getExpiresIn());

            verify(userServiceClient).getUserForAuth("user@example.com");
            verify(passwordEncoder).matches("password123", "hashed_password");
            verify(jwtUtil).generateToken("testuser", "USER");
        }

        @Test
        @DisplayName("Should login admin user successfully")
        void login_AdminUser_ReturnsAuthResponseWithAdminRole() {
            // Arrange
            LoginRequest request = new LoginRequest("admin@example.com", "admin123");
            AuthUserResponse authUser = AuthUserResponse.builder()
                    .userId(1L)
                    .email("admin@example.com")
                    .username("admin")
                    .passwordHash("hashed_admin123")
                    .role("ADMIN")
                    .build();

            when(userServiceClient.getUserForAuth("admin@example.com")).thenReturn(Optional.of(authUser));
            when(passwordEncoder.matches("admin123", "hashed_admin123")).thenReturn(true);

            // Act
            AuthResponse response = authService.login(request);

            // Assert
            assertNotNull(response);
            assertEquals("admin", response.getUsername());
            assertEquals("ADMIN", response.getRole());
        }

        @Test
        @DisplayName("Should throw exception for non-existent user")
        void login_NonExistentUser_ThrowsIllegalArgumentException() {
            // Arrange
            LoginRequest request = new LoginRequest("nonexistent@example.com", "password");
            when(userServiceClient.getUserForAuth("nonexistent@example.com")).thenReturn(null);

            // Act & Assert
            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class, () -> authService.login(request));
            assertEquals("Invalid email or password", exception.getMessage());

            verify(userServiceClient).getUserForAuth("nonexistent@example.com");
            verify(passwordEncoder, never()).matches(anyString(), anyString());
        }

        @Test
        @DisplayName("Should throw exception for wrong password")
        void login_WrongPassword_ThrowsIllegalArgumentException() {
            // Arrange
            LoginRequest request = new LoginRequest("user@example.com", "wrongpassword");
            AuthUserResponse authUser = AuthUserResponse.builder()
                    .userId(1L)
                    .email("user@example.com")
                    .username("testuser")
                    .passwordHash("hashed_correctpassword")
                    .role("USER")
                    .build();

            when(userServiceClient.getUserForAuth("user@example.com")).thenReturn(Optional.of(authUser));
            when(passwordEncoder.matches("wrongpassword", "hashed_correctpassword"))
                    .thenReturn(false);

            // Act & Assert
            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class, () -> authService.login(request));
            assertEquals("Invalid email or password", exception.getMessage());

            verify(passwordEncoder).matches("wrongpassword", "hashed_correctpassword");
            verify(jwtUtil, never()).generateToken(anyString(), anyString());
        }
    }

    @Nested
    @DisplayName("Integration Flow Tests")
    class IntegrationFlowTests {

        @Test
        @DisplayName("Should handle user service returning user with all fields")
        void login_UserServiceReturnsCompleteUser_HandlesCorrectly() {
            // Arrange
            LoginRequest request = new LoginRequest("complete@example.com", "password");
            AuthUserResponse authUser = AuthUserResponse.builder()
                    .userId(99L)
                    .email("complete@example.com")
                    .username("completeuser")
                    .passwordHash("complete_hash")
                    .role("USER")
                    .build();

            when(userServiceClient.getUserForAuth("complete@example.com")).thenReturn(Optional.of(authUser));
            when(passwordEncoder.matches("password", "complete_hash")).thenReturn(true);

            // Act
            AuthResponse response = authService.login(request);

            // Assert
            assertNotNull(response);
            assertEquals("completeuser", response.getUsername());
            assertEquals("USER", response.getRole());
        }

        @Test
        @DisplayName("Should generate correct JWT token parameters")
        void login_GeneratesTokenWithCorrectParameters() {
            // Arrange
            LoginRequest request = new LoginRequest("user@example.com", "password");
            AuthUserResponse authUser = AuthUserResponse.builder()
                    .userId(1L)
                    .email("user@example.com")
                    .username("specificuser")
                    .passwordHash("hash")
                    .role("ADMIN")
                    .build();

            when(userServiceClient.getUserForAuth("user@example.com")).thenReturn(Optional.of(authUser));
            when(passwordEncoder.matches("password", "hash")).thenReturn(true);

            // Act
            authService.login(request);

            // Assert - verify token generated with correct username and role
            verify(jwtUtil).generateToken("specificuser", "ADMIN");
        }
    }
}
