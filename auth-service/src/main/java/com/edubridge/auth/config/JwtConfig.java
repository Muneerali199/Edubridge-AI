package com.edubridge.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

/**
 * JWT configuration properties
 */
@Configuration
@Getter
public class JwtConfig {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;
}
