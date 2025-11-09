package com.edubridge.common.exception;

import com.edubridge.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for all REST controllers
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(EdubridgeException.class)
    public ResponseEntity<ApiResponse<Void>> handleEdubridgeException(EdubridgeException ex) {
        log.error("EduBridge exception: {}", ex.getMessage(), ex);
        
        ApiResponse.ErrorDetails error = ApiResponse.ErrorDetails.builder()
                .code(ex.getErrorCode())
                .message(ex.getMessage())
                .build();
        
        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(ApiResponse.error(error));
    }
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        
        ApiResponse.ErrorDetails error = ApiResponse.ErrorDetails.builder()
                .code(ex.getErrorCode())
                .message(ex.getMessage())
                .build();
        
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(error));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ApiResponse.ErrorDetails error = ApiResponse.ErrorDetails.builder()
                .code("VALIDATION_ERROR")
                .message("Validation failed")
                .details(errors)
                .build();
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(error));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        
        ApiResponse.ErrorDetails error = ApiResponse.ErrorDetails.builder()
                .code("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred. Please try again later.")
                .build();
        
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(error));
    }
}
