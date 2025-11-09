package com.edubridge.course.dto;

import com.edubridge.common.enums.ContentType;
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
public class LectureDTO {
    private UUID id;
    private String title;
    private String description;
    private ContentType contentType;
    private String contentUrl;
    private String textContent;
    private Integer orderIndex;
    private Integer durationSeconds;
    private Boolean isPreview;
    private String voiceTranscript;
    private String audioUrl;
    private Boolean isFree;
    private Instant createdAt;
    private Instant updatedAt;
}
