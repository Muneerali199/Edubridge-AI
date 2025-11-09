package com.edubridge.auth.entity;

import com.edubridge.common.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "users", schema = "auth")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(length = 20)
    private String phone;
    
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    
    @Column(nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private UserRole role;
    
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
    
    @Column(name = "is_verified")
    @Builder.Default
    private Boolean isVerified = false;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
    
    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private Instant updatedAt = Instant.now();
    
    @Column(name = "last_login_at")
    private Instant lastLoginAt;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "profile_json", columnDefinition = "jsonb")
    private Map<String, Object> profileJson;
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
