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
import java.nio.file.Files;

@RestController
@RequestMapping("/api/spring")
public class SttController {

    private final FastApiService fastApiService;
    private final TextFileService textFileService;
    private final UserRepository userRepository;

    public SttController(FastApiService fastApiService,
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
        if (multipartFile == null || multipartFile.isEmpty()) {
            return ResponseEntity.badRequest().body("파일이 비어 있습니다.");
        }

        File tempFile = null;
        boolean saved = false;

        try {
            // MultipartFile을 임시 파일로 저장
            String suffix = "";
            String originalName = multipartFile.getOriginalFilename();
            if (originalName != null && originalName.contains(".")) {
                suffix = originalName.substring(originalName.lastIndexOf(".")); // 확장자 유지
            }
            tempFile = File.createTempFile("upload-", multipartFile.getOriginalFilename());
            multipartFile.transferTo(tempFile);

            // FastAPI API 호출 (DTO로 받기)
            TextFileDto responseDto = fastApiService.uploadAudioFileToFastApi(tempFile);

            // DB에 저장
            HttpSession session = request.getSession(false);
            Long userId = (session != null) ? (Long) session.getAttribute("userId") : null;
            User user = null;
            if (userId != null) {
                user = userRepository.findById(userId).orElse(null);
            }
            if (userId != null) {
                textFileService.saveTextFile(multipartFile.getOriginalFilename(),
                        responseDto.getTranscript(),
                        responseDto.getSummary1(),
                        responseDto.getSummary2(),
                        responseDto.getSummary3(),
                        user
                );
                saved = true;
            }

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
        } finally {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile.toPath());
                } catch (IOException ignored) {
                }
            }
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
