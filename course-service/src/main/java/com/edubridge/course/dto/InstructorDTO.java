package com.edubridge.course.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstructorDTO {
    private UUID id;
    private UUID userId;
    private String name;
    private String email;
    private String profileImageUrl;
    private String headline;
    private String biography;
    private String websiteUrl;
    private String linkedinUrl;
    private String twitterUrl;
    private String youtubeUrl;
    private List<String> expertise = new ArrayList<>();
    private Integer totalStudents;
    private Integer totalCourses;
    private Integer totalReviews;
    private BigDecimal averageRating;
    private Boolean isVerified;
    private Boolean isTopInstructor;
    private Boolean voiceTutorEnabled;
    private String voiceName;
    private Boolean aiAssistantEnabled;
    private Instant createdAt;
    private Instant updatedAt;
}
