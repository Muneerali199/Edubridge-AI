package com.edubridge.course.dto;

import com.edubridge.common.enums.CourseLevel;
import com.edubridge.common.enums.CourseStatus;
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
public class CourseDTO {
    private UUID id;
    private String title;
    private String subtitle;
    private String description;
    private UUID instructorId;
    private String instructorName;
    private CourseLevel level;
    private CourseStatus status;
    private String thumbnailUrl;
    private String previewVideoUrl;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private Integer durationHours;
    private Integer totalLectures;
    private Integer totalEnrollments;
    private BigDecimal averageRating;
    private Integer totalReviews;
    private List<String> tags = new ArrayList<>();
    private List<String> learningOutcomes = new ArrayList<>();
    private List<String> requirements = new ArrayList<>();
    private Boolean voiceEnabled;
    private String voiceLanguage;
    private Boolean aiTutorEnabled;
    private Instant publishedAt;
    private Instant createdAt;
    private Instant updatedAt;
    private List<ModuleDTO> modules = new ArrayList<>();
}
