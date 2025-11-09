package com.edubridge.course.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleDTO {
    private UUID id;
    private String title;
    private String description;
    private Integer orderIndex;
    private Integer durationMinutes;
    private Instant createdAt;
    private Instant updatedAt;
    private List<LectureDTO> lectures = new ArrayList<>();
}
