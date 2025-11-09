package com.edubridge.course.controller;

import com.edubridge.common.dto.ApiResponse;
import com.edubridge.common.enums.CourseStatus;
import com.edubridge.course.dto.CourseDTO;
import com.edubridge.course.dto.CreateCourseRequest;
import com.edubridge.course.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Courses", description = "Course management endpoints")
public class CourseController {
    
    private final CourseService courseService;
    
    @PostMapping("/create")
    @Operation(summary = "Create a new course")
    public ResponseEntity<ApiResponse<CourseDTO>> createCourse(
            @Valid @RequestBody CreateCourseRequest request) {
        log.info("Creating course: {}", request.getTitle());
        CourseDTO course = courseService.createCourse(request);
        return ResponseEntity.ok(ApiResponse.success(course, "Course created successfully"));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get course by ID")
    public ResponseEntity<ApiResponse<CourseDTO>> getCourse(@PathVariable UUID id) {
        CourseDTO course = courseService.getCourseById(id);
        return ResponseEntity.ok(ApiResponse.success(course, "Course retrieved successfully"));
    }
    
    @GetMapping
    @Operation(summary = "Get all published courses")
    public ResponseEntity<ApiResponse<Page<CourseDTO>>> getAllCourses(Pageable pageable) {
        Page<CourseDTO> courses = courseService.getCourses(CourseStatus.PUBLISHED, pageable);
        return ResponseEntity.ok(ApiResponse.success(courses, "Courses retrieved successfully"));
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search courses")
    public ResponseEntity<ApiResponse<Page<CourseDTO>>> searchCourses(
            @RequestParam String keyword,
            Pageable pageable) {
        Page<CourseDTO> courses = courseService.searchCourses(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(courses, "Search completed"));
    }
    
    @GetMapping("/instructor/{instructorId}")
    @Operation(summary = "Get courses by instructor")
    public ResponseEntity<ApiResponse<Page<CourseDTO>>> getCoursesByInstructor(
            @PathVariable UUID instructorId,
            Pageable pageable) {
        Page<CourseDTO> courses = courseService.getCoursesByInstructor(instructorId, pageable);
        return ResponseEntity.ok(ApiResponse.success(courses, "Courses retrieved successfully"));
    }
    
    @PostMapping("/{id}/publish")
    @Operation(summary = "Publish a course")
    public ResponseEntity<ApiResponse<CourseDTO>> publishCourse(@PathVariable UUID id) {
        CourseDTO course = courseService.publishCourse(id);
        return ResponseEntity.ok(ApiResponse.success(course, "Course published successfully"));
    }
    
    @PostMapping("/{id}/enable-voice")
    @Operation(summary = "Enable voice for course")
    public ResponseEntity<ApiResponse<CourseDTO>> enableVoice(
            @PathVariable UUID id,
            @RequestParam(required = false, defaultValue = "en-US-Neural2-A") String voiceName) {
        CourseDTO course = courseService.enableVoice(id, voiceName);
        return ResponseEntity.ok(ApiResponse.success(course, "Voice enabled successfully"));
    }
}
