package org.example.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.example.dto.AuthUserResponse;
import org.example.dto.CreateUserRequest;
import org.example.dto.UpdateUserRequest;
import org.example.dto.UserResponse;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Unit tests for UserService.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setPasswordHash("hashedpassword");
        testUser.setLatitude(40.7128);
        testUser.setLongitude(-74.0060);
        testUser.setRole("USER");
        testUser.setCreatedAt(LocalDateTime.now());
    }

    @Nested
    @DisplayName("Create User Tests")
    class CreateUserTests {

        @Test
        @DisplayName("Should create user successfully")
        void createUser_ValidRequest_ReturnsUserResponse() {
            // Arrange
            CreateUserRequest request =
                    new CreateUserRequest("new@example.com", "newuser", "password123", 40.0, -74.0, "USER");
            when(userRepository.existsByUsername("newuser")).thenReturn(false);
            when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User savedUser = invocation.getArgument(0);
                savedUser.setUserId(1L);
                return savedUser;
            });

            // Act
            Optional<UserResponse> result = userService.createUser(request);

            // Assert
            assertTrue(result.isPresent());
            assertEquals("new@example.com", result.get().getEmail());
            assertEquals("newuser", result.get().getUsername());
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should return empty when username already exists")
        void createUser_DuplicateUsername_ReturnsEmpty() {
            // Arrange
            CreateUserRequest request =
                    new CreateUserRequest("new@example.com", "existinguser", "password123", null, null, null);
            when(userRepository.existsByUsername("existinguser")).thenReturn(true);

            // Act
            Optional<UserResponse> result = userService.createUser(request);

            // Assert
            assertTrue(result.isEmpty());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should return empty when email already exists")
        void createUser_DuplicateEmail_ReturnsEmpty() {
            // Arrange
            CreateUserRequest request =
                    new CreateUserRequest("existing@example.com", "newuser", "password123", null, null, null);
            when(userRepository.existsByUsername("newuser")).thenReturn(false);
            when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

            // Act
            Optional<UserResponse> result = userService.createUser(request);

            // Assert
            assertTrue(result.isEmpty());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should default role to USER when not provided")
        void createUser_NoRole_DefaultsToUser() {
            // Arrange
            CreateUserRequest request =
                    new CreateUserRequest("new@example.com", "newuser", "password123", null, null, null);
            when(userRepository.existsByUsername("newuser")).thenReturn(false);
            when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User savedUser = invocation.getArgument(0);
                savedUser.setUserId(1L);
                return savedUser;
            });

            // Act
            Optional<UserResponse> result = userService.createUser(request);

            // Assert
            assertTrue(result.isPresent());
            assertEquals("USER", result.get().getRole());
        }
    }

    @Nested
    @DisplayName("Get User Tests")
    class GetUserTests {

        @Test
        @DisplayName("Should get user by ID")
        void getUserById_UserExists_ReturnsUserResponse() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            // Act
            UserResponse result = userService.getUserById(1L);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getUserId());
            assertEquals("testuser", result.getUsername());
        }

        @Test
        @DisplayName("Should throw exception when user not found by ID")
        void getUserById_UserNotFound_ThrowsException() {
            // Arrange
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class, () -> userService.getUserById(999L));
            assertTrue(exception.getMessage().contains("User not found"));
        }

        @Test
        @DisplayName("Should get user by username")
        void getUserByUsername_UserExists_ReturnsUserResponse() {
            // Arrange
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            // Act
            UserResponse result = userService.getUserByUsername("testuser");

            // Assert
            assertEquals("testuser", result.getUsername());
        }

        @Test
        @DisplayName("Should get user by email")
        void getUserByEmail_UserExists_ReturnsUserResponse() {
            // Arrange
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

            // Act
            UserResponse result = userService.getUserByEmail("test@example.com");

            // Assert
            assertEquals("test@example.com", result.getEmail());
        }

        @Test
        @DisplayName("Should get all users")
        void getAllUsers_ReturnsAllUsers() {
            // Arrange
            User user2 = new User();
            user2.setUserId(2L);
            user2.setEmail("user2@example.com");
            user2.setUsername("user2");
            user2.setRole("USER");
            when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, user2));

            // Act
            List<UserResponse> result = userService.getAllUsers();

            // Assert
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Should return empty list when no users")
        void getAllUsers_NoUsers_ReturnsEmptyList() {
            // Arrange
            when(userRepository.findAll()).thenReturn(Collections.emptyList());

            // Act
            List<UserResponse> result = userService.getAllUsers();

            // Assert
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Update User Tests")
    class UpdateUserTests {

        @Test
        @DisplayName("Should update user successfully")
        void updateUser_ValidRequest_ReturnsUpdatedUser() {
            // Arrange
            UpdateUserRequest request = new UpdateUserRequest("newemail@example.com", "newusername", 41.0, -75.0);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.existsByEmail("newemail@example.com")).thenReturn(false);
            when(userRepository.existsByUsername("newusername")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // Act
            UserResponse result = userService.updateUser(1L, request);

            // Assert
            assertNotNull(result);
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when updating to existing email")
        void updateUser_DuplicateEmail_ThrowsException() {
            // Arrange
            UpdateUserRequest request = new UpdateUserRequest("existing@example.com", null, null, null);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

            // Act & Assert
            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class, () -> userService.updateUser(1L, request));
            assertEquals("Email already exists", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when updating to existing username")
        void updateUser_DuplicateUsername_ThrowsException() {
            // Arrange
            UpdateUserRequest request = new UpdateUserRequest(null, "existingusername", null, null);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.existsByUsername("existingusername")).thenReturn(true);

            // Act & Assert
            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class, () -> userService.updateUser(1L, request));
            assertEquals("Username already exists", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Delete User Tests")
    class DeleteUserTests {

        @Test
        @DisplayName("Should delete user successfully")
        void deleteUser_UserExists_DeletesUser() {
            // Arrange
            when(userRepository.existsById(1L)).thenReturn(true);
            doNothing().when(userRepository).deleteById(1L);

            // Act
            userService.deleteUser(1L);

            // Assert
            verify(userRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent user")
        void deleteUser_UserNotFound_ThrowsException() {
            // Arrange
            when(userRepository.existsById(999L)).thenReturn(false);

            // Act & Assert
            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(999L));
            assertTrue(exception.getMessage().contains("User not found"));
        }
    }

    @Nested
    @DisplayName("Get User For Auth Tests")
    class GetUserForAuthTests {

        @Test
        @DisplayName("Should return AuthUserResponse for valid email")
        void getUserForAuth_ValidEmail_ReturnsAuthUserResponse() {
            // Arrange
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

            // Act
            Optional<AuthUserResponse> result = userService.getUserForAuth("test@example.com");

            // Assert
            assertTrue(result.isPresent());
            assertEquals("test@example.com", result.get().getEmail());
            assertEquals("hashedpassword", result.get().getPasswordHash());
        }

        @Test
        @DisplayName("Should throw exception when user not found for auth")
        void getUserForAuth_UserNotFound_ThrowsException() {
            // Arrange
            when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> userService.getUserForAuth("notfound@example.com"));
        }
    }
}
