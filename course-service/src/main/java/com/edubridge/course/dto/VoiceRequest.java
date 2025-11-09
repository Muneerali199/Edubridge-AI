package com.edubridge.course.dto;

import jakarta.validation.constraints.NotBlank;

public record VoiceRequest(
    @NotBlank(message = "Text is required")
    String text,
    
    String languageCode,
    
    String voiceName
) {
    public VoiceRequest {
        if (languageCode == null || languageCode.isBlank()) {
            languageCode = "en-US";
        }
        if (voiceName == null || voiceName.isBlank()) {
            voiceName = "en-US-Neural2-A";
        }
    }
}
