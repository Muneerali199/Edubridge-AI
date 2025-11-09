package com.edubridge.auth.controller;

import com.edubridge.auth.dto.UpdateProfileRequest;
import com.edubridge.auth.dto.UserProfileResponse;
import com.edubridge.auth.service.ProfileService;
import com.edubridge.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for user profile management
 */
@Slf4j
@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
@Tag(name = "Profile", description = "User profile management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class ProfileController {
    
    private final ProfileService profileService;
    
    @GetMapping
    @Operation(summary = "Get user profile", description = "Get current user's profile information")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        log.info("Fetching profile for user ID: {}", userId);
        
        UserProfileResponse profile = profileService.getUserProfile(userId);
        
        return ResponseEntity.ok(ApiResponse.success(profile, "Profile retrieved successfully"));
    }
    
    @PutMapping
    @Operation(summary = "Update profile", description = "Update user profile information (email cannot be changed)")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request) {
        Long userId = Long.parseLong(authentication.getName());
        log.info("Updating profile for user ID: {}", userId);
        
        UserProfileResponse profile = profileService.updateProfile(userId, request);
        
        return ResponseEntity.ok(ApiResponse.success(profile, "Profile updated successfully"));
    }
}
