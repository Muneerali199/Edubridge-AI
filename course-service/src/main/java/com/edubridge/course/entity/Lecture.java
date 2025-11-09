package com.edubridge.course.entity;

import com.edubridge.common.enums.ContentType;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "lectures")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Lecture {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    @ToString.Exclude
    private Module module;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentType contentType;
    
    @Column(name = "content_url")
    private String contentUrl;
    
    @Column(columnDefinition = "TEXT")
    private String textContent;
    
    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;
    
    @Column(name = "duration_seconds")
    private Integer durationSeconds;
    
    @Column(name = "is_preview")
    private Boolean isPreview = false;
    
    @Column(name = "voice_transcript", columnDefinition = "TEXT")
    private String voiceTranscript;
    
    @Column(name = "audio_url")
    private String audioUrl;
    
    @Column(name = "is_free")
    private Boolean isFree = false;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
