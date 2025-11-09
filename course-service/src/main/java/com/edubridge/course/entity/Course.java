package com.edubridge.course.entity;

import com.edubridge.common.enums.CourseLevel;
import com.edubridge.common.enums.CourseStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "courses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Course {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(length = 500)
    private String subtitle;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "instructor_id", nullable = false)
    private UUID instructorId;
    
    @Column(name = "instructor_name")
    private String instructorName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseLevel level = CourseLevel.BEGINNER;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseStatus status = CourseStatus.DRAFT;
    
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;
    
    @Column(name = "preview_video_url")
    private String previewVideoUrl;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "discount_price", precision = 10, scale = 2)
    private BigDecimal discountPrice;
    
    @Column(name = "duration_hours")
    private Integer durationHours;
    
    @Column(name = "total_lectures")
    private Integer totalLectures = 0;
    
    @Column(name = "total_enrollments")
    private Integer totalEnrollments = 0;
    
    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;
    
    @Column(name = "total_reviews")
    private Integer totalReviews = 0;
    
    @ElementCollection
    @CollectionTable(name = "course_tags", joinColumns = @JoinColumn(name = "course_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "course_learning_outcomes", joinColumns = @JoinColumn(name = "course_id"))
    @Column(name = "outcome", length = 500)
    private List<String> learningOutcomes = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "course_requirements", joinColumns = @JoinColumn(name = "course_id"))
    @Column(name = "requirement", length = 500)
    private List<String> requirements = new ArrayList<>();
    
    @Column(name = "voice_enabled")
    private Boolean voiceEnabled = false;
    
    @Column(name = "voice_language")
    private String voiceLanguage = "en-US";
    
    @Column(name = "ai_tutor_enabled")
    private Boolean aiTutorEnabled = false;
    
    @Column(name = "published_at")
    private Instant publishedAt;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Module> modules = new ArrayList<>();
}
