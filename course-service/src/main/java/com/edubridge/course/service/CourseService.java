package com.edubridge.course.service;

import com.edubridge.common.enums.CourseStatus;
import com.edubridge.course.dto.CourseDTO;
import com.edubridge.course.dto.CreateCourseRequest;
import com.edubridge.course.entity.Course;
import com.edubridge.course.entity.Instructor;
import com.edubridge.course.repository.CourseRepository;
import com.edubridge.course.repository.InstructorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseService {
    
    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;
    private final VoiceService voiceService;
    
    @Transactional
    public CourseDTO createCourse(CreateCourseRequest request) {
        log.info("Creating course: {}", request.getTitle());
        
        Instructor instructor = instructorRepository.findById(request.getInstructorId())
            .orElseThrow(() -> new RuntimeException("Instructor not found"));
        
        Course course = Course.builder()
            .title(request.getTitle())
            .subtitle(request.getSubtitle())
            .description(request.getDescription())
            .instructorId(instructor.getId())
            .instructorName(instructor.getName())
            .level(request.getLevel())
            .status(CourseStatus.DRAFT)
            .thumbnailUrl(request.getThumbnailUrl())
            .previewVideoUrl(request.getPreviewVideoUrl())
            .price(request.getPrice())
            .discountPrice(request.getDiscountPrice())
            .durationHours(request.getDurationHours())
            .tags(request.getTags())
            .learningOutcomes(request.getLearningOutcomes())
            .requirements(request.getRequirements())
            .voiceEnabled(request.getVoiceEnabled())
            .voiceLanguage(request.getVoiceLanguage())
            .aiTutorEnabled(request.getAiTutorEnabled())
            .build();
        
        Course saved = courseRepository.save(course);
        return mapToDTO(saved);
    }
    
    public CourseDTO getCourseById(UUID id) {
        Course course = courseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Course not found"));
        return mapToDTO(course);
    }
    
    public Page<CourseDTO> getCourses(CourseStatus status, Pageable pageable) {
        return courseRepository.findByStatus(status, pageable)
            .map(this::mapToDTO);
    }
    
    public Page<CourseDTO> searchCourses(String keyword, Pageable pageable) {
        return courseRepository.searchCourses(CourseStatus.PUBLISHED, keyword, pageable)
            .map(this::mapToDTO);
    }
    
    public Page<CourseDTO> getCoursesByInstructor(UUID instructorId, Pageable pageable) {
        return courseRepository.findByInstructorId(instructorId, pageable)
            .map(this::mapToDTO);
    }
    
    @Transactional
    public CourseDTO publishCourse(UUID id) {
        Course course = courseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Course not found"));
        
        course.setStatus(CourseStatus.PUBLISHED);
        course.setPublishedAt(Instant.now());
        
        Course saved = courseRepository.save(course);
        return mapToDTO(saved);
    }
    
    @Transactional
    public CourseDTO enableVoice(UUID id, String voiceName) {
        Course course = courseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Course not found"));
        
        course.setVoiceEnabled(true);
        // Generate audio for existing text content if needed
        
        Course saved = courseRepository.save(course);
        return mapToDTO(saved);
    }
    
    private CourseDTO mapToDTO(Course course) {
        return CourseDTO.builder()
            .id(course.getId())
            .title(course.getTitle())
            .subtitle(course.getSubtitle())
            .description(course.getDescription())
            .instructorId(course.getInstructorId())
            .instructorName(course.getInstructorName())
            .level(course.getLevel())
            .status(course.getStatus())
            .thumbnailUrl(course.getThumbnailUrl())
            .previewVideoUrl(course.getPreviewVideoUrl())
            .price(course.getPrice())
            .discountPrice(course.getDiscountPrice())
            .durationHours(course.getDurationHours())
            .totalLectures(course.getTotalLectures())
            .totalEnrollments(course.getTotalEnrollments())
            .averageRating(course.getAverageRating())
            .totalReviews(course.getTotalReviews())
            .tags(course.getTags())
            .learningOutcomes(course.getLearningOutcomes())
            .requirements(course.getRequirements())
            .voiceEnabled(course.getVoiceEnabled())
            .voiceLanguage(course.getVoiceLanguage())
            .aiTutorEnabled(course.getAiTutorEnabled())
            .publishedAt(course.getPublishedAt())
            .createdAt(course.getCreatedAt())
            .updatedAt(course.getUpdatedAt())
            .build();
    }
}
