package com.example.TEAM_B_backend.service;

import com.example.TEAM_B_backend.dto.TranscriptionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TranscriptionService {

    public void save(TranscriptionDto transcriptionDto) {
        // 저장 로직 (ex. DB 저장, 로그 등)
        System.out.println("userID: " + transcriptionDto.getUserId());
        System.out.println("userID: " + transcriptionDto.getTranscription());
        // 나중에 repository.save(...) 연결
    }
}
