package com.edubridge.course.controller;

import com.edubridge.common.dto.ApiResponse;
import com.edubridge.course.dto.VoiceRequest;
import com.edubridge.course.dto.VoiceResponse;
import com.edubridge.course.service.VoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/voice")
@RequiredArgsConstructor
@Tag(name = "Voice", description = "Voice integration endpoints (TTS & STT)")
public class VoiceController {
    
    private final VoiceService voiceService;
    
    @PostMapping("/text-to-speech")
    @Operation(summary = "Convert text to speech")
    public ResponseEntity<ApiResponse<VoiceResponse>> textToSpeech(
            @Valid @RequestBody VoiceRequest request) {
        log.info("Converting text to speech: length={}", request.text().length());
        
        String audioUrl = voiceService.textToSpeech(
            request.text(),
            request.languageCode(),
            request.voiceName()
        );
        
        VoiceResponse response = new VoiceResponse(audioUrl, request.text());
        return ResponseEntity.ok(ApiResponse.success(response, "Audio generated successfully"));
    }
    
    @PostMapping("/speech-to-text")
    @Operation(summary = "Convert speech to text")
    public ResponseEntity<ApiResponse<VoiceResponse>> speechToText(
            @RequestParam("audio") MultipartFile audioFile,
            @RequestParam(defaultValue = "en-US") String languageCode) {
        log.info("Converting speech to text: file={}", audioFile.getOriginalFilename());
        
        // TODO: Upload audio file and get URL
        String audioUrl = "uploaded-audio-url";
        String transcript = voiceService.speechToText(audioUrl, languageCode);
        
        VoiceResponse response = new VoiceResponse(audioUrl, transcript);
        return ResponseEntity.ok(ApiResponse.success(response, "Transcription completed"));
    }
    
    @GetMapping("/voices")
    @Operation(summary = "Get available voices")
    public ResponseEntity<ApiResponse<List<VoiceService.VoiceOption>>> getAvailableVoices(
            @RequestParam(defaultValue = "en-US") String languageCode) {
        List<VoiceService.VoiceOption> voices = voiceService.getAvailableVoices(languageCode);
        return ResponseEntity.ok(ApiResponse.success(voices, "Voices retrieved successfully"));
    }
}
