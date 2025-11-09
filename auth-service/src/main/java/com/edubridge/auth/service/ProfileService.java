package com.edubridge.auth.service;

import com.edubridge.auth.dto.UpdateProfileRequest;
import com.edubridge.auth.dto.UserProfileResponse;
import com.edubridge.auth.entity.User;
import com.edubridge.auth.repository.UserRepository;
import com.edubridge.common.exception.EdubridgeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Service for user profile management
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {
    
    private final UserRepository userRepository;
    
    /**
     * Get user profile
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(Long userId) {
        log.info("Fetching profile for user ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EdubridgeException(
                        "User not found",
                        "USER_NOT_FOUND",
                        404
                ));
        
        return mapToProfileResponse(user);
    }
    
    /**
     * Update user profile (email cannot be changed)
     */
    @Transactional
    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        log.info("Updating profile for user ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EdubridgeException(
                        "User not found",
                        "USER_NOT_FOUND",
                        404
                ));
        
        // Check if phone is being changed and if it's already taken by another user
        if (request.getPhone() != null && !request.getPhone().equals(user.getPhone())) {
            if (userRepository.existsByPhone(request.getPhone())) {
                throw new EdubridgeException(
                        "Phone number already in use",
                        "PHONE_ALREADY_EXISTS",
                        409
                );
            }
        }
        
        // Update profile
        String[] names = splitName(user.getName(), request.getFirstName(), request.getLastName());
        user.setName(names[0] + " " + names[1]);
        user.setPhone(request.getPhone());
        user.setUpdatedAt(Instant.now());
        
        user = userRepository.save(user);
        log.info("Profile updated successfully for user ID: {}", userId);
        
        return mapToProfileResponse(user);
    }
    
    /**
     * Map User entity to UserProfileResponse
     */
    private UserProfileResponse mapToProfileResponse(User user) {
        String[] names = splitName(user.getName(), null, null);
        
        return UserProfileResponse.builder()
                .id(user.getId())
                .firstName(names[0])
                .lastName(names[1])
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .isVerified(user.getIsVerified())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
    
    /**
     * Split full name into first and last name
     */
    private String[] splitName(String fullName, String firstName, String lastName) {
        if (firstName != null && lastName != null) {
            return new String[]{firstName, lastName};
        }
        
        if (fullName == null || fullName.trim().isEmpty()) {
            return new String[]{"", ""};
        }
        
        String[] parts = fullName.trim().split("\\s+", 2);
        return new String[]{
                parts[0],
                parts.length > 1 ? parts[1] : ""
        };
    }
}
