package org.example.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.CreateUserRequest;
import org.example.dto.UpdateUserRequest;
import org.example.dto.UserResponse;
import org.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

/**
 * Unit tests for UserController using MockMvc.
 */
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private UserResponse mockUserResponse;

    @BeforeEach
    void setUp() {
        mockUserResponse = new UserResponse();
        mockUserResponse.setUserId(1L);
        mockUserResponse.setEmail("test@example.com");
        mockUserResponse.setUsername("testuser");
        mockUserResponse.setLatitude(40.7128);
        mockUserResponse.setLongitude(-74.0060);
        mockUserResponse.setRole("USER");
        mockUserResponse.setCreatedAt(LocalDateTime.now());
    }

    @Nested
    @DisplayName("Create User Endpoint Tests")
    class CreateUserEndpointTests {

        @Test
        @DisplayName("POST /api/users - should return 201 with UserResponse when successful")
        void createUser_ValidRequest_Returns201() throws Exception {
            // Arrange
            CreateUserRequest request = new CreateUserRequest(
                    "test@example.com", "testuser", "password123", 40.7128, -74.0060, "USER");
            when(userService.createUser(any(CreateUserRequest.class))).thenReturn(Optional.of(mockUserResponse));

            // Act & Assert
            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.userId").value(1))
                    .andExpect(jsonPath("$.email").value("test@example.com"))
                    .andExpect(jsonPath("$.username").value("testuser"));
        }

        @Test
        @DisplayName("POST /api/users - should return 400 when user creation fails")
        void createUser_DuplicateUser_Returns400() throws Exception {
            // Arrange
            CreateUserRequest request = new CreateUserRequest(
                    "existing@example.com", "existinguser", "password123", null, null, null);
            when(userService.createUser(any(CreateUserRequest.class))).thenReturn(Optional.empty());

            // Act & Assert
            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Get User Endpoint Tests")
    class GetUserEndpointTests {

        @Test
        @DisplayName("GET /api/users/{userId} - should return user when found")
        void getUserById_UserExists_ReturnsUser() throws Exception {
            // Arrange
            when(userService.getUserById(1L)).thenReturn(mockUserResponse);

            // Act & Assert
            mockMvc.perform(get("/api/users/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.userId").value(1))
                    .andExpect(jsonPath("$.email").value("test@example.com"));
        }

        @Test
        @DisplayName("GET /api/users/{userId} - should return 404 when not found")
        void getUserById_UserNotFound_Returns404() throws Exception {
            // Arrange
            when(userService.getUserById(999L)).thenThrow(new IllegalArgumentException("User not found"));

            // Act & Assert
            mockMvc.perform(get("/api/users/999"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("GET /api/users/username/{username} - should return user when found")
        void getUserByUsername_UserExists_ReturnsUser() throws Exception {
            // Arrange
            when(userService.getUserByUsername("testuser")).thenReturn(mockUserResponse);

            // Act & Assert
            mockMvc.perform(get("/api/users/username/testuser"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("testuser"));
        }

        @Test
        @DisplayName("GET /api/users/email/{email} - should return user when found")
        void getUserByEmail_UserExists_ReturnsUser() throws Exception {
            // Arrange
            when(userService.getUserByEmail("test@example.com")).thenReturn(mockUserResponse);

            // Act & Assert
            mockMvc.perform(get("/api/users/email/test@example.com"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("test@example.com"));
        }

        @Test
        @DisplayName("GET /api/users - should return all users")
        void getAllUsers_ReturnsUserList() throws Exception {
            // Arrange
            UserResponse user2 = new UserResponse();
            user2.setUserId(2L);
            user2.setEmail("user2@example.com");
            user2.setUsername("user2");
            when(userService.getAllUsers()).thenReturn(Arrays.asList(mockUserResponse, user2));

            // Act & Assert
            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        @DisplayName("GET /api/users - should return empty list when no users")
        void getAllUsers_NoUsers_ReturnsEmptyList() throws Exception {
            // Arrange
            when(userService.getAllUsers()).thenReturn(Collections.emptyList());

            // Act & Assert
            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    @Nested
    @DisplayName("Update User Endpoint Tests")
    class UpdateUserEndpointTests {

        @Test
        @DisplayName("PUT /api/users/{userId} - should return updated user")
        void updateUser_ValidRequest_ReturnsUpdatedUser() throws Exception {
            // Arrange
            UpdateUserRequest request = new UpdateUserRequest("newemail@example.com", "newusername", 41.0, -75.0);
            UserResponse updatedResponse = new UserResponse();
            updatedResponse.setUserId(1L);
            updatedResponse.setEmail("newemail@example.com");
            updatedResponse.setUsername("newusername");
            when(userService.updateUser(anyLong(), any(UpdateUserRequest.class))).thenReturn(updatedResponse);

            // Act & Assert
            mockMvc.perform(put("/api/users/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("newemail@example.com"))
                    .andExpect(jsonPath("$.username").value("newusername"));
        }

        @Test
        @DisplayName("PUT /api/users/{userId} - should return 400 when update fails")
        void updateUser_InvalidRequest_Returns400() throws Exception {
            // Arrange
            UpdateUserRequest request = new UpdateUserRequest("existing@example.com", null, null, null);
            when(userService.updateUser(anyLong(), any(UpdateUserRequest.class)))
                    .thenThrow(new IllegalArgumentException("Email already exists"));

            // Act & Assert
            mockMvc.perform(put("/api/users/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Delete User Endpoint Tests")
    class DeleteUserEndpointTests {

        @Test
        @DisplayName("DELETE /api/users/{userId} - should return 204 when successful")
        void deleteUser_UserExists_Returns204() throws Exception {
            // Arrange
            doNothing().when(userService).deleteUser(1L);

            // Act & Assert
            mockMvc.perform(delete("/api/users/1"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("DELETE /api/users/{userId} - should return 404 when not found")
        void deleteUser_UserNotFound_Returns404() throws Exception {
            // Arrange
            doThrow(new IllegalArgumentException("User not found")).when(userService).deleteUser(999L);

            // Act & Assert
            mockMvc.perform(delete("/api/users/999"))
                    .andExpect(status().isNotFound());
        }
    }
}
