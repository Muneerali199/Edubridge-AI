package com.edubridge.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    private String tokenType = "Bearer";
    private UserDto user;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDto {
        private UUID id;
        private String name;
        private String email;
        private String role;
        private Boolean isVerified;
        private Instant createdAt;
    }
}
