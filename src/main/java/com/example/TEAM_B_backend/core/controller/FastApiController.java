package com.example.TEAM_B_backend.core.controller;

import com.example.TEAM_B_backend.archive.dto.TextFileDto;
import com.example.TEAM_B_backend.core.dto.PauseRequestDto;
import com.example.TEAM_B_backend.core.dto.SpeedRequestDto;
import com.example.TEAM_B_backend.core.service.FastApiService;
import com.example.TEAM_B_backend.archive.fileService.TextFileService;
import com.example.TEAM_B_backend.user.entity.User;
import com.example.TEAM_B_backend.user.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/fastapi")
public class FastApiController {

    private final FastApiService fastApiService;
    private final TextFileService textFileService;
    private final UserRepository userRepository;

    public FastApiController(FastApiService fastApiService,
                             TextFileService textFileService,
                             UserRepository userRepository) {
        this.fastApiService = fastApiService;
        this.textFileService = textFileService;
        this.userRepository = userRepository;
    }

    // 업로드 컨트롤러
    @PostMapping("/upload")
    public ResponseEntity<?> uploadAndCallFastApi(@RequestParam("file") MultipartFile multipartFile,
                                                  HttpServletRequest request) {
        if (multipartFile.isEmpty()) {
            return ResponseEntity.badRequest().body("파일이 비어 있습니다.");
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        Long userId = (Long) session.getAttribute("userId");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            // MultipartFile을 임시 파일로 저장
            File tempFile = File.createTempFile("upload-", multipartFile.getOriginalFilename());
            multipartFile.transferTo(tempFile);

            // FastAPI API 호출 (DTO로 받기)
            TextFileDto responseDto = fastApiService.uploadAudioFileToFastApi(tempFile);

            // DB에 저장
            textFileService.saveTextFile(multipartFile.getOriginalFilename(),
                    responseDto.getTranscript(),
                    responseDto.getSummary1(),
                    responseDto.getSummary2(),
                    responseDto.getSummary3(),
                    user);

            // 임시 파일 삭제
            tempFile.delete();

            // 결과 반환
            return ResponseEntity.ok(responseDto);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("파일 처리 중 오류 발생");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("FastAPI 요청 실패: " + e.getMessage());
        }
    }

    // 속도 측정 컨트롤러
    @PostMapping("/analyze-speed")
    public ResponseEntity<?> analyzeSpeed(@RequestBody SpeedRequestDto dto) {
        return ResponseEntity.ok(fastApiService.callAnalyzeSpeed(dto));
    }

    // 공백 측정 컨트롤러
    @PostMapping("/analyze-pause")
    public ResponseEntity<?> analyzePause(@RequestBody PauseRequestDto dto) {
        return ResponseEntity.ok(fastApiService.callAnalyzePause(dto));
    }
}
