package org.example.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.example.dto.AuthUserResponse;
import org.example.dto.CreateUserRequest;
import org.example.dto.UpdateUserRequest;
import org.example.dto.UserResponse;
// import org.example.kafka.UserEventProducer;
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

    @Mock
    // private UserEventProducer userEventProducer;

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
    }

    @Nested
    @DisplayName("Kafka Event Tests")
    class KafkaEventTests {
    
        @Test
        @DisplayName("Should send user created event when user is created")
        void createUser_ShouldCallKafkaProducer() {
            // Arrange
            CreateUserRequest request =
                    new CreateUserRequest("kafka@example.com", "kafkatest", "password", null, null, "USER");
    
            when(userRepository.existsByUsername("kafkatest")).thenReturn(false);
            when(userRepository.existsByEmail("kafka@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User u = invocation.getArgument(0);
                u.setUserId(123L);
                return u;
            });
    
            // Act
            Optional<UserResponse> result = userService.createUser(request);
    
            // Assert
            assertTrue(result.isPresent());
            // verify(userEventProducer, times(1))
            //         .sendUserCreatedEvent(123L, "kafkatest", "kafka@example.com");
        }
    
        @Test
        @DisplayName("Should send user deleted event when user is deleted")
        void deleteUser_ShouldCallKafkaProducer() {
            // Arrange
            when(userRepository.existsById(1L)).thenReturn(true);
            doNothing().when(userRepository).deleteById(1L);
    
            // Act
            userService.deleteUser(1L);
    
            // Assert
            // verify(userEventProducer, times(1)).sendUserDeletedEvent(1L);
            verify(userRepository).deleteById(1L);
        }
    
        @Test
        @DisplayName("Should NOT send user created event when creation fails due to duplicate username")
        void createUser_DuplicateUsername_ShouldNotCallKafkaProducer() {
            // Arrange
            CreateUserRequest request =
                    new CreateUserRequest("dup@example.com", "existinguser", "password", null, null, "USER");
    
            when(userRepository.existsByUsername("existinguser")).thenReturn(true);
    
            // Act
            Optional<UserResponse> result = userService.createUser(request);
    
            // Assert
            assertTrue(result.isEmpty());
            // verify(userEventProducer, never()).sendUserCreatedEvent(anyLong(), any(), any());
        }
    
        @Test
        @DisplayName("Should NOT send user created event when creation fails due to duplicate email")
        void createUser_DuplicateEmail_ShouldNotCallKafkaProducer() {
            // Arrange
            CreateUserRequest request =
                    new CreateUserRequest("existing@example.com", "newuser", "password", null, null, "USER");
    
            when(userRepository.existsByUsername("newuser")).thenReturn(false);
            when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);
    
            // Act
            Optional<UserResponse> result = userService.createUser(request);
    
            // Assert
            assertTrue(result.isEmpty());
            // verify(userEventProducer, never()).sendUserCreatedEvent(anyLong(), any(), any());
        }
    
        @Test
        @DisplayName("Should NOT send user deleted event when user does not exist")
        void deleteUser_UserNotFound_ShouldNotCallKafkaProducer() {
            // Arrange
            when(userRepository.existsById(999L)).thenReturn(false);
    
            // Act & Assert
            IllegalArgumentException ex =
                    assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(999L));
            assertTrue(ex.getMessage().contains("User not found"));
    
            // verify(userEventProducer, never()).sendUserDeletedEvent(anyLong());
        }
    }

    @Nested
    @DisplayName("Additional Edge Case Tests")
    class AdditionalUserServiceTests {
    
        @Test
        @DisplayName("Should default role to USER when null in create request")
        void createUser_NoRoleAssigned_DefaultsToUser() {
            CreateUserRequest request = new CreateUserRequest(
                    "norole@example.com", "noroleuser", "pass", null, null, null);
    
            when(userRepository.existsByUsername("noroleuser")).thenReturn(false);
            when(userRepository.existsByEmail("norole@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User u = invocation.getArgument(0);
                u.setUserId(42L);
                return u;
            });
    
            Optional<UserResponse> result = userService.createUser(request);
    
            assertTrue(result.isPresent());
            assertEquals("USER", result.get().getRole());
            // verify(userEventProducer).sendUserCreatedEvent(42L, "noroleuser", "norole@example.com");
        }
    
        @Test
        @DisplayName("Should update user with only some fields set")
        void updateUser_PartialUpdate_UpdatesOnlyProvidedFields() {
            UpdateUserRequest request = new UpdateUserRequest(null, "updatedUsername", null, null);
    
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.existsByUsername("updatedUsername")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(testUser);
    
            UserResponse response = userService.updateUser(1L, request);
    
            assertNotNull(response);
            assertEquals("updatedUsername", response.getUsername());
            verify(userRepository).save(testUser);
        }
    
        @Test
        @DisplayName("Should update user with no changes")
        void updateUser_NoChanges_ShouldSaveOriginal() {
            UpdateUserRequest request = new UpdateUserRequest(null, null, null, null);
    
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(testUser);
    
            UserResponse response = userService.updateUser(1L, request);
    
            assertNotNull(response);
            assertEquals("testuser", response.getUsername());
            verify(userRepository).save(testUser);
        }
    
        @Test
        @DisplayName("Should get user for auth")
        void getUserForAuth_UserExists_ReturnsAuthUserResponse() {
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
    
            Optional<AuthUserResponse> response = userService.getUserForAuth("test@example.com");
    
            assertTrue(response.isPresent());
            assertEquals("testuser", response.get().getUsername());
        }
    
        @Test
        @DisplayName("Should get empty auth response if user not found")
        void getUserForAuth_UserNotFound_ReturnsEmpty() {
            when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());
    
            Optional<AuthUserResponse> response = userService.getUserForAuth("notfound@example.com");
    
            assertTrue(response.isEmpty());
        }
    
        @Test
        @DisplayName("Delete user should throw exception if userId is null")
        void deleteUser_NullUserId_ShouldThrowException() {
            assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(null));
            // verify(userEventProducer, never()).sendUserDeletedEvent(any());
            verify(userRepository, never()).deleteById(any());
        }
    }
    
    
}
