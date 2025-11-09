package com.edubridge.course.dto;

import com.edubridge.common.enums.CourseLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCourseRequest {
    
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;
    
    @Size(max = 500, message = "Subtitle must not exceed 500 characters")
    private String subtitle;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotNull(message = "Instructor ID is required")
    private UUID instructorId;
    
    @NotNull(message = "Course level is required")
    private CourseLevel level;
    
    private String thumbnailUrl;
    private String previewVideoUrl;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private Integer durationHours;
    
    @Builder.Default
    private List<String> tags = new ArrayList<>();
    
    @Builder.Default
    private List<String> learningOutcomes = new ArrayList<>();
    
    @Builder.Default
    private List<String> requirements = new ArrayList<>();
    
    @Builder.Default
    private Boolean voiceEnabled = false;
    
    @Builder.Default
    private String voiceLanguage = "en-US";
    
    @Builder.Default
    private Boolean aiTutorEnabled = false;
}
