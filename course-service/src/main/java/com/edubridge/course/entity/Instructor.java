package com.edubridge.course.entity;

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
@Table(name = "instructors")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Instructor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(name = "profile_image_url")
    private String profileImageUrl;
    
    @Column(length = 500)
    private String headline;
    
    @Column(columnDefinition = "TEXT")
    private String biography;
    
    @Column(name = "website_url")
    private String websiteUrl;
    
    @Column(name = "linkedin_url")
    private String linkedinUrl;
    
    @Column(name = "twitter_url")
    private String twitterUrl;
    
    @Column(name = "youtube_url")
    private String youtubeUrl;
    
    @ElementCollection
    @CollectionTable(name = "instructor_expertise", joinColumns = @JoinColumn(name = "instructor_id"))
    @Column(name = "expertise")
    private List<String> expertise = new ArrayList<>();
    
    @Column(name = "total_students")
    private Integer totalStudents = 0;
    
    @Column(name = "total_courses")
    private Integer totalCourses = 0;
    
    @Column(name = "total_reviews")
    private Integer totalReviews = 0;
    
    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;
    
    @Column(name = "is_verified")
    private Boolean isVerified = false;
    
    @Column(name = "is_top_instructor")
    private Boolean isTopInstructor = false;
    
    @Column(name = "voice_tutor_enabled")
    private Boolean voiceTutorEnabled = false;
    
    @Column(name = "voice_name")
    private String voiceName; // e.g., "en-US-Neural2-A"
    
    @Column(name = "ai_assistant_enabled")
    private Boolean aiAssistantEnabled = false;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
