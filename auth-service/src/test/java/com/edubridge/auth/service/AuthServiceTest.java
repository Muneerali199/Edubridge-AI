package com.edubridge.auth.service;

import com.edubridge.auth.dto.LoginRequest;
import com.edubridge.auth.dto.RegisterRequest;
import com.edubridge.auth.dto.AuthResponse;
import com.edubridge.auth.entity.User;
import com.edubridge.auth.repository.UserRepository;
import com.edubridge.auth.util.JwtUtil;
import com.edubridge.common.enums.UserRole;
import com.edubridge.common.exception.EdubridgeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtUtil jwtUtil;
    
    @InjectMocks
    private AuthService authService;
    
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User testUser;
    
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
        
        testUser = User.builder()
                .id(UUID.randomUUID())
                .name("John Doe")
                .email("john@example.com")
                .passwordHash("hashed_password")
                .role(UserRole.STUDENT)
                .isActive(true)
                .isVerified(false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }
    
    @Test
    @DisplayName("Should register new user successfully")
    void shouldRegisterNewUserSuccessfully() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtUtil.generateToken(any(UUID.class), anyString(), anyString())).thenReturn("access_token");
        when(jwtUtil.generateRefreshToken(any(UUID.class), anyString())).thenReturn("refresh_token");
        
        // When
        AuthResponse response = authService.register(registerRequest);
        
        // Then
        assertNotNull(response);
        assertEquals("access_token", response.getAccessToken());
        assertEquals("refresh_token", response.getRefreshToken());
        assertNotNull(response.getUser());
        assertEquals("john@example.com", response.getUser().getEmail());
        
        verify(userRepository).existsByEmail("john@example.com");
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("password123");
    }
    
    @Test
    @DisplayName("Should throw exception when user already exists")
    void shouldThrowExceptionWhenUserAlreadyExists() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        
        // When & Then
        EdubridgeException exception = assertThrows(
            EdubridgeException.class,
            () -> authService.register(registerRequest)
        );
        
        assertEquals("USER_ALREADY_EXISTS", exception.getErrorCode());
        assertEquals(409, exception.getHttpStatus());
        
        verify(userRepository).existsByEmail("john@example.com");
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    @DisplayName("Should login user successfully")
    void shouldLoginUserSuccessfully() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtUtil.generateToken(any(UUID.class), anyString(), anyString())).thenReturn("access_token");
        when(jwtUtil.generateRefreshToken(any(UUID.class), anyString())).thenReturn("refresh_token");
        
        // When
        AuthResponse response = authService.login(loginRequest);
        
        // Then
        assertNotNull(response);
        assertEquals("access_token", response.getAccessToken());
        assertEquals("refresh_token", response.getRefreshToken());
        
        verify(userRepository).findByEmail("john@example.com");
        verify(passwordEncoder).matches("password123", "hashed_password");
        verify(userRepository).save(any(User.class)); // Update last login
    }
    
    @Test
    @DisplayName("Should throw exception when user not found during login")
    void shouldThrowExceptionWhenUserNotFoundDuringLogin() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        
        // When & Then
        EdubridgeException exception = assertThrows(
            EdubridgeException.class,
            () -> authService.login(loginRequest)
        );
        
        assertEquals("INVALID_CREDENTIALS", exception.getErrorCode());
        assertEquals(401, exception.getHttpStatus());
        
        verify(userRepository).findByEmail("john@example.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }
    
    @Test
    @DisplayName("Should throw exception when password is invalid")
    void shouldThrowExceptionWhenPasswordIsInvalid() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        
        // When & Then
        EdubridgeException exception = assertThrows(
            EdubridgeException.class,
            () -> authService.login(loginRequest)
        );
        
        assertEquals("INVALID_CREDENTIALS", exception.getErrorCode());
        assertEquals(401, exception.getHttpStatus());
        
        verify(userRepository).findByEmail("john@example.com");
        verify(passwordEncoder).matches("password123", "hashed_password");
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    @DisplayName("Should throw exception when account is deactivated")
    void shouldThrowExceptionWhenAccountIsDeactivated() {
        // Given
        testUser.setIsActive(false);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        
        // When & Then
        EdubridgeException exception = assertThrows(
            EdubridgeException.class,
            () -> authService.login(loginRequest)
        );
        
        assertEquals("ACCOUNT_DEACTIVATED", exception.getErrorCode());
        assertEquals(403, exception.getHttpStatus());
        
        verify(userRepository).findByEmail("john@example.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }
}
