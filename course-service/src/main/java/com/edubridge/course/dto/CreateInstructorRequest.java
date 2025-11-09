package com.edubridge.course.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateInstructorRequest {
    
    @NotNull(message = "User ID is required")
    private UUID userId;
    
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    private String profileImageUrl;
    
    @Size(max = 500, message = "Headline must not exceed 500 characters")
    private String headline;
    
    private String biography;
    
    private String websiteUrl;
    private String linkedinUrl;
    private String twitterUrl;
    private String youtubeUrl;
    
    @Builder.Default
    private List<String> expertise = new ArrayList<>();
    
    @Builder.Default
    private Boolean voiceTutorEnabled = false;
    
    private String voiceName;
    
    @Builder.Default
    private Boolean aiAssistantEnabled = false;
}
