package com.edubridge.auth.controller;

import com.edubridge.auth.dto.AuthResponse;
import com.edubridge.auth.dto.LoginRequest;
import com.edubridge.auth.dto.RegisterRequest;
import com.edubridge.auth.service.AuthService;
import com.edubridge.common.enums.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@DisplayName("AuthController Tests")
class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private AuthService authService;
    
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private AuthResponse authResponse;
    
    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .name("John Doe")
                .email("john@example.com")
                .password("password123")
                .role(UserRole.STUDENT)
                .build();
        
        loginRequest = LoginRequest.builder()
                .email("john@example.com")
                .password("password123")
                .build();
        
        AuthResponse.UserDto userDto = AuthResponse.UserDto.builder()
                .id(UUID.randomUUID())
                .name("John Doe")
                .email("john@example.com")
                .role("STUDENT")
                .isVerified(false)
                .createdAt(Instant.now())
                .build();
        
        authResponse = AuthResponse.builder()
                .accessToken("access_token")
                .refreshToken("refresh_token")
                .expiresIn(86400000L)
                .user(userDto)
                .build();
    }
    
    @Test
    @DisplayName("Should register user successfully")
    void shouldRegisterUserSuccessfully() throws Exception {
        // Given
        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);
        
        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("access_token"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh_token"))
                .andExpect(jsonPath("$.data.user.email").value("john@example.com"));
    }
    
    @Test
    @DisplayName("Should return validation error for invalid email")
    void shouldReturnValidationErrorForInvalidEmail() throws Exception {
        // Given
        registerRequest.setEmail("invalid-email");
        
        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Should login user successfully")
    void shouldLoginUserSuccessfully() throws Exception {
        // Given
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);
        
        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("access_token"));
    }
    
    @Test
    @DisplayName("Should return validation error for missing password")
    void shouldReturnValidationErrorForMissingPassword() throws Exception {
        // Given
        loginRequest.setPassword(null);
        
        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Should return health status")
    void shouldReturnHealthStatus() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/auth/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Healthy"));
    }
}
