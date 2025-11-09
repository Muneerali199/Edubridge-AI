package com.edubridge.auth.service;

import com.edubridge.auth.dto.*;
import com.edubridge.auth.entity.User;
import com.edubridge.auth.repository.UserRepository;
import com.edubridge.auth.util.JwtUtil;
import com.edubridge.common.exception.EdubridgeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Authentication service handling user registration, login, and token management
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    /**
     * Register a new user
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());
        
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EdubridgeException(
                "User with email " + request.getEmail() + " already exists",
                "USER_ALREADY_EXISTS",
                409
            );
        }
        
        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
            throw new EdubridgeException(
                "User with phone " + request.getPhone() + " already exists",
                "USER_ALREADY_EXISTS",
                409
            );
        }
        
        // Create new user
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .isActive(true)
                .isVerified(false) // Email verification required
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        
        user = userRepository.save(user);
        log.info("User registered successfully with ID: {}", user.getId());
        
        // Generate tokens
        String accessToken = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getEmail());
        
        return buildAuthResponse(user, accessToken, refreshToken);
    }
    
    /**
     * Login user
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EdubridgeException(
                    "Invalid email or password",
                    "INVALID_CREDENTIALS",
                    401
                ));
        
        // Check if user is active
        if (!user.getIsActive()) {
            throw new EdubridgeException(
                "Account is deactivated. Please contact support.",
                "ACCOUNT_DEACTIVATED",
                403
            );
        }
        
        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new EdubridgeException(
                "Invalid email or password",
                "INVALID_CREDENTIALS",
                401
            );
        }
        
        // Update last login time
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);
        
        log.info("User logged in successfully: {}", user.getEmail());
        
        // Generate tokens
        String accessToken = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getEmail());
        
        return buildAuthResponse(user, accessToken, refreshToken);
    }
    
    /**
     * Refresh access token using refresh token
     */
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        log.info("Refreshing token");
        
        try {
            // Validate refresh token
            String email = jwtUtil.extractEmail(request.getRefreshToken());
            
            // Find user
            User user = userRepository.findByEmailAndIsActiveTrue(email)
                    .orElseThrow(() -> new EdubridgeException(
                        "User not found or inactive",
                        "INVALID_TOKEN",
                        401
                    ));
            
            // Generate new access token
            String accessToken = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name());
            String newRefreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getEmail());
            
            log.info("Token refreshed successfully for user: {}", user.getEmail());
            
            return buildAuthResponse(user, accessToken, newRefreshToken);
            
        } catch (Exception e) {
            log.error("Error refreshing token: {}", e.getMessage());
            throw new EdubridgeException(
                "Invalid or expired refresh token",
                "INVALID_TOKEN",
                401
            );
        }
    }
    
    /**
     * Build auth response with tokens and user info
     */
    private AuthResponse buildAuthResponse(User user, String accessToken, String refreshToken) {
        AuthResponse.UserDto userDto = AuthResponse.UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .isVerified(user.getIsVerified())
                .createdAt(user.getCreatedAt())
                .build();
        
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(86400000L) // 24 hours in milliseconds
                .user(userDto)
                .build();
    }
}
