package com.edubridge.common.exception;

/**
 * Exception thrown when a requested resource is not found
 */
public class ResourceNotFoundException extends EdubridgeException {
    
    public ResourceNotFoundException(String resourceType, String identifier) {
        super(
            String.format("%s not found with identifier: %s", resourceType, identifier),
            "RESOURCE_NOT_FOUND",
            404
        );
    }
    
    public ResourceNotFoundException(String message) {
        super(message, "RESOURCE_NOT_FOUND", 404);
    }
}
