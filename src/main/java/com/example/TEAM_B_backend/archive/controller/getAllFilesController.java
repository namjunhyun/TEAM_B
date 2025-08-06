package com.example.TEAM_B_backend.archive.controller;

import com.example.TEAM_B_backend.archive.dto.FileDetailResponseDto;
import com.example.TEAM_B_backend.archive.dto.TextFileDto;
import com.example.TEAM_B_backend.archive.fileService.TextFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class getAllFilesController {

    private final TextFileService textFileService;

    // 파일 목록 조회: userId 기반
    @GetMapping
    public ResponseEntity<?> getFileList(@RequestParam Long userId) {
        try {
            List<TextFileDto> fileList = textFileService.getFileList(userId);
            return  ResponseEntity.ok(fileList);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", "해당 사용자의 파일 목록을 찾을 수 없습니다"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", "서버 오류가 발생했습니다."));
        }
    }

    // 파일 상세 조회: fileId 기반 (원문, 요약 세가지, 코칭은 제외!)
    @GetMapping("/{fileId}/details")
    public ResponseEntity<?> getFileDetails(@PathVariable Long fileId) {
        try {
            FileDetailResponseDto details = textFileService.getFileDetails(fileId);
            return ResponseEntity.ok(details);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", "해당 사용자의 파일 목록을 찾을 수 없습니다"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", "서버 오류가 발생했습니다."));
        }
    }
}
