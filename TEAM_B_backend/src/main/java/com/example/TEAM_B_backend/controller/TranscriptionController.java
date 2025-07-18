package com.example.TEAM_B_backend.controller;

import com.example.TEAM_B_backend.dto.TranscriptionDto;
import com.example.TEAM_B_backend.service.TranscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/summary")
@RequiredArgsConstructor
public class TranscriptionController {

    private final TranscriptionService transcriptionService;

    @PostMapping
    public ResponseEntity<String> saveTranscription(@RequestBody TranscriptionDto transcriptionDto) {
        transcriptionService.save(transcriptionDto);
        return ResponseEntity.ok("변환 및 요약 저장");
    }


}
