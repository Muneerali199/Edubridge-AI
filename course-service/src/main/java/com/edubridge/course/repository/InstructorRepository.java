package com.edubridge.course.repository;

import com.edubridge.course.entity.Instructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, UUID> {
    
    Optional<Instructor> findByUserId(UUID userId);
    
    Optional<Instructor> findByEmail(String email);
    
    Page<Instructor> findByIsVerified(Boolean isVerified, Pageable pageable);
    
    Page<Instructor> findByIsTopInstructor(Boolean isTopInstructor, Pageable pageable);
    
    @Query("SELECT i FROM Instructor i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(i.headline) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Instructor> searchInstructors(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT i FROM Instructor i WHERE i.voiceTutorEnabled = true")
    List<Instructor> findVoiceEnabledInstructors();
    
    @Query("SELECT i FROM Instructor i ORDER BY i.totalStudents DESC")
    List<Instructor> findTopInstructors(Pageable pageable);
}
