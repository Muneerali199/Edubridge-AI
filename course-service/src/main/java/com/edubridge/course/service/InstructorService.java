package com.edubridge.course.service;

import com.edubridge.course.dto.CreateInstructorRequest;
import com.edubridge.course.dto.InstructorDTO;
import com.edubridge.course.entity.Instructor;
import com.edubridge.course.repository.InstructorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstructorService {
    
    private final InstructorRepository instructorRepository;
    
    @Transactional
    public InstructorDTO createInstructor(CreateInstructorRequest request) {
        log.info("Creating instructor: {}", request.getName());
        
        if (instructorRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Instructor with this email already exists");
        }
        
        Instructor instructor = Instructor.builder()
            .userId(request.getUserId())
            .name(request.getName())
            .email(request.getEmail())
            .profileImageUrl(request.getProfileImageUrl())
            .headline(request.getHeadline())
            .biography(request.getBiography())
            .websiteUrl(request.getWebsiteUrl())
            .linkedinUrl(request.getLinkedinUrl())
            .twitterUrl(request.getTwitterUrl())
            .youtubeUrl(request.getYoutubeUrl())
            .expertise(request.getExpertise())
            .voiceTutorEnabled(request.getVoiceTutorEnabled())
            .voiceName(request.getVoiceName())
            .aiAssistantEnabled(request.getAiAssistantEnabled())
            .build();
        
        Instructor saved = instructorRepository.save(instructor);
        return mapToDTO(saved);
    }
    
    public InstructorDTO getInstructorById(UUID id) {
        Instructor instructor = instructorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Instructor not found"));
        return mapToDTO(instructor);
    }
    
    public Page<InstructorDTO> getAllInstructors(Pageable pageable) {
        return instructorRepository.findAll(pageable)
            .map(this::mapToDTO);
    }
    
    public List<InstructorDTO> getTopInstructors(int limit) {
        return instructorRepository.findTopInstructors(PageRequest.of(0, limit))
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public InstructorDTO enableVoiceTutor(UUID id, String voiceName) {
        Instructor instructor = instructorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Instructor not found"));
        
        instructor.setVoiceTutorEnabled(true);
        instructor.setVoiceName(voiceName);
        instructor.setAiAssistantEnabled(true);
        
        Instructor saved = instructorRepository.save(instructor);
        return mapToDTO(saved);
    }
    
    private InstructorDTO mapToDTO(Instructor instructor) {
        return InstructorDTO.builder()
            .id(instructor.getId())
            .userId(instructor.getUserId())
            .name(instructor.getName())
            .email(instructor.getEmail())
            .profileImageUrl(instructor.getProfileImageUrl())
            .headline(instructor.getHeadline())
            .biography(instructor.getBiography())
            .websiteUrl(instructor.getWebsiteUrl())
            .linkedinUrl(instructor.getLinkedinUrl())
            .twitterUrl(instructor.getTwitterUrl())
            .youtubeUrl(instructor.getYoutubeUrl())
            .expertise(instructor.getExpertise())
            .totalStudents(instructor.getTotalStudents())
            .totalCourses(instructor.getTotalCourses())
            .totalReviews(instructor.getTotalReviews())
            .averageRating(instructor.getAverageRating())
            .isVerified(instructor.getIsVerified())
            .isTopInstructor(instructor.getIsTopInstructor())
            .voiceTutorEnabled(instructor.getVoiceTutorEnabled())
            .voiceName(instructor.getVoiceName())
            .aiAssistantEnabled(instructor.getAiAssistantEnabled())
            .createdAt(instructor.getCreatedAt())
            .updatedAt(instructor.getUpdatedAt())
            .build();
    }
}
