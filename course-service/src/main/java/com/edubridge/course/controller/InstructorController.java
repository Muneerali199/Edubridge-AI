package com.edubridge.course.controller;

import com.edubridge.common.dto.ApiResponse;
import com.edubridge.course.dto.InstructorDTO;
import com.edubridge.course.dto.CreateInstructorRequest;
import com.edubridge.course.service.InstructorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/instructors")
@RequiredArgsConstructor
@Tag(name = "Instructors", description = "Instructor management endpoints")
public class InstructorController {
    
    private final InstructorService instructorService;
    
    @PostMapping
    @Operation(summary = "Create instructor profile")
    public ResponseEntity<ApiResponse<InstructorDTO>> createInstructor(
            @Valid @RequestBody CreateInstructorRequest request) {
        log.info("Creating instructor: {}", request.getName());
        InstructorDTO instructor = instructorService.createInstructor(request);
        return ResponseEntity.ok(ApiResponse.success(instructor, "Instructor created successfully"));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get instructor by ID")
    public ResponseEntity<ApiResponse<InstructorDTO>> getInstructor(@PathVariable UUID id) {
        InstructorDTO instructor = instructorService.getInstructorById(id);
        return ResponseEntity.ok(ApiResponse.success(instructor, "Instructor retrieved successfully"));
    }
    
    @GetMapping
    @Operation(summary = "Get all instructors")
    public ResponseEntity<ApiResponse<Page<InstructorDTO>>> getAllInstructors(Pageable pageable) {
        Page<InstructorDTO> instructors = instructorService.getAllInstructors(pageable);
        return ResponseEntity.ok(ApiResponse.success(instructors, "Instructors retrieved successfully"));
    }
    
    @GetMapping("/top")
    @Operation(summary = "Get top instructors")
    public ResponseEntity<ApiResponse<List<InstructorDTO>>> getTopInstructors(
            @RequestParam(defaultValue = "10") int limit) {
        List<InstructorDTO> instructors = instructorService.getTopInstructors(limit);
        return ResponseEntity.ok(ApiResponse.success(instructors, "Top instructors retrieved"));
    }
    
    @PostMapping("/{id}/enable-voice-tutor")
    @Operation(summary = "Enable AI voice tutor for instructor")
    public ResponseEntity<ApiResponse<InstructorDTO>> enableVoiceTutor(
            @PathVariable UUID id,
            @RequestParam String voiceName) {
        log.info("Enabling voice tutor for instructor: {}", id);
        InstructorDTO instructor = instructorService.enableVoiceTutor(id, voiceName);
        return ResponseEntity.ok(ApiResponse.success(instructor, "Voice tutor enabled"));
    }
}
