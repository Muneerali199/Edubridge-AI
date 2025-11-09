package com.edubridge.common.exception;

import lombok.Getter;

/**
 * Base exception for all EduBridge custom exceptions
 */
@Getter
public class EdubridgeException extends RuntimeException {
    
    private final String errorCode;
    private final int httpStatus;
    
    public EdubridgeException(String message, String errorCode, int httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
    
    public EdubridgeException(String message, String errorCode, int httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
}
