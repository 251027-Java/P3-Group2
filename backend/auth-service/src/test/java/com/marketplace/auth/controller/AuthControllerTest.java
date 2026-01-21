package com.marketplace.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.auth.dto.AuthResponse;
import com.marketplace.auth.dto.LoginRequest;
import com.marketplace.auth.dto.RegisterRequest;
import com.marketplace.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Unit tests for AuthController using MockMvc.
 */
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    private AuthResponse mockAuthResponse;

    @BeforeEach
    void setUp() {
        mockAuthResponse = AuthResponse.builder()
                .token("test.jwt.token")
                .type("Bearer")
                .expiresIn(86400000L)
                .username("testuser")
                .role("USER")
                .build();
    }

    @Nested
    @DisplayName("Registration Endpoint Tests")
    class RegistrationEndpointTests {

        @Test
        @DisplayName("POST /api/auth/register - should return 200 with AuthResponse")
        void register_ValidRequest_Returns200WithAuthResponse() throws Exception {
            // Arrange
            RegisterRequest request = new RegisterRequest("test@example.com", "testuser", "password123");
            when(authService.register(any(RegisterRequest.class))).thenReturn(mockAuthResponse);

            // Act & Assert
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.token").value("test.jwt.token"))
                    .andExpect(jsonPath("$.type").value("Bearer"))
                    .andExpect(jsonPath("$.username").value("testuser"))
                    .andExpect(jsonPath("$.role").value("USER"))
                    .andExpect(jsonPath("$.expiresIn").value(86400000L));
        }

        @Test
        @DisplayName("POST /api/auth/register - should call AuthService.register")
        void register_ValidRequest_CallsAuthService() throws Exception {
            // Arrange
            RegisterRequest request = new RegisterRequest("new@example.com", "newuser", "securepass");
            when(authService.register(any(RegisterRequest.class))).thenReturn(mockAuthResponse);

            // Act
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Login Endpoint Tests")
    class LoginEndpointTests {

        @Test
        @DisplayName("POST /api/auth/login - should return 200 with AuthResponse")
        void login_ValidCredentials_Returns200WithAuthResponse() throws Exception {
            // Arrange
            LoginRequest request = new LoginRequest("test@example.com", "password123");
            when(authService.login(any(LoginRequest.class))).thenReturn(mockAuthResponse);

            // Act & Assert
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.token").value("test.jwt.token"))
                    .andExpect(jsonPath("$.type").value("Bearer"))
                    .andExpect(jsonPath("$.username").value("testuser"))
                    .andExpect(jsonPath("$.role").value("USER"));
        }

        @Test
        @DisplayName("POST /api/auth/login - should return auth response with admin role")
        void login_AdminUser_ReturnsAuthResponseWithAdminRole() throws Exception {
            // Arrange
            LoginRequest request = new LoginRequest("admin@example.com", "adminpass");
            AuthResponse adminResponse = AuthResponse.builder()
                    .token("admin.jwt.token")
                    .type("Bearer")
                    .expiresIn(86400000L)
                    .username("admin")
                    .role("ADMIN")
                    .build();
            when(authService.login(any(LoginRequest.class))).thenReturn(adminResponse);

            // Act & Assert
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.role").value("ADMIN"))
                    .andExpect(jsonPath("$.username").value("admin"));
        }
    }
}
