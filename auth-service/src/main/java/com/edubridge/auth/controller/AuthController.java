package com.edubridge.auth.controller;

import com.edubridge.auth.dto.AuthResponse;
import com.edubridge.auth.dto.LoginRequest;
import com.edubridge.auth.dto.RefreshTokenRequest;
import com.edubridge.auth.dto.RegisterRequest;
import com.edubridge.auth.service.AuthService;
import com.edubridge.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication endpoints
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User registration, login, and token management")
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Create a new user account")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registration request received for email: {}", request.getEmail());
        
        AuthResponse response = authService.register(request);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "User registered successfully"));
    }
    
    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticate user and return JWT tokens")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request received for email: {}", request.getEmail());
        
        AuthResponse response = authService.login(request);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }
    
    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Get new access token using refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Token refresh request received");
        
        AuthResponse response = authService.refreshToken(request);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Token refreshed successfully"));
    }
    
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if the auth service is running")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Auth service is running", "Healthy"));
    }
}
