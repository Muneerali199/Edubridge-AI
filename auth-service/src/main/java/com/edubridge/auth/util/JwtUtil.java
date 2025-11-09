package com.edubridge.auth.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.edubridge.auth.config.JwtConfig;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Utility class for JWT token operations
 */
@Slf4j
@Component
public class JwtUtil {
    
    private final SecretKey secretKey;
    private final Long expiration;
    private final Long refreshExpiration;
    
    public JwtUtil(JwtConfig jwtConfig) {
        this.secretKey = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
        this.expiration = jwtConfig.getExpiration();
        this.refreshExpiration = jwtConfig.getRefreshExpiration();
    }
    
    /**
     * Generate access token for a user
     */
    public String generateToken(UUID userId, String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId.toString());
        claims.put("email", email);
        claims.put("role", role);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * Generate refresh token
     */
    public String generateRefreshToken(UUID userId, String email) {
        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId.toString())
                .claim("tokenType", "refresh")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * Extract email from token
     */
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }
    
    /**
     * Extract user ID from token
     */
    public UUID extractUserId(String token) {
        String userIdStr = extractClaims(token).get("userId", String.class);
        return UUID.fromString(userIdStr);
    }
    
    /**
     * Extract role from token
     */
    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }
    
    /**
     * Extract all claims from token
     */
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * Validate token
     */
    public boolean validateToken(String token, String email) {
        try {
            final String extractedEmail = extractEmail(token);
            return (extractedEmail.equals(email) && !isTokenExpired(token));
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Check if token is expired
     */
    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }
    
    /**
     * Get token expiration date
     */
    public Date getExpirationDate(String token) {
        return extractClaims(token).getExpiration();
    }
    
    /**
     * Validate token structure without checking expiration
     */
    public boolean isValidTokenStructure(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT structure: {}", e.getMessage());
            return false;
        }
    }
}
