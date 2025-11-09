package com.edubridge.course.repository;

import com.edubridge.common.enums.CourseLevel;
import com.edubridge.common.enums.CourseStatus;
import com.edubridge.course.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {
    
    Page<Course> findByStatus(CourseStatus status, Pageable pageable);
    
    Page<Course> findByInstructorId(UUID instructorId, Pageable pageable);
    
    Page<Course> findByLevel(CourseLevel level, Pageable pageable);
    
    @Query("SELECT c FROM Course c WHERE c.status = :status AND " +
           "(LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Course> searchCourses(@Param("status") CourseStatus status, 
                               @Param("keyword") String keyword, 
                               Pageable pageable);
    
    @Query("SELECT c FROM Course c WHERE :tag MEMBER OF c.tags AND c.status = :status")
    Page<Course> findByTag(@Param("tag") String tag, 
                          @Param("status") CourseStatus status, 
                          Pageable pageable);
    
    @Query("SELECT c FROM Course c WHERE c.status = :status ORDER BY c.totalEnrollments DESC")
    List<Course> findTopCourses(@Param("status") CourseStatus status, Pageable pageable);
    
    @Query("SELECT c FROM Course c WHERE c.voiceEnabled = true AND c.status = :status")
    Page<Course> findVoiceEnabledCourses(@Param("status") CourseStatus status, Pageable pageable);
}
