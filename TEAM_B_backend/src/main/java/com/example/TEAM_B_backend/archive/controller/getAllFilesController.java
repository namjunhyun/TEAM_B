package com.example.TEAM_B_backend.archive.controller;

import com.example.TEAM_B_backend.archive.dto.FileDetailResponseDto;
import com.example.TEAM_B_backend.archive.dto.TextFileDto;
import com.example.TEAM_B_backend.archive.fileService.TextFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class getAllFilesController {

    private final TextFileService textFileService;

    // 파일 목록 조회: userId 기반
    @GetMapping
    public ResponseEntity<List<TextFileDto>> getFileList(@RequestParam Long userId) {
        return ResponseEntity.ok(textFileService.getFileList(userId));
    }

    // 파일 상세 조회: fileId 기반 (원문, 요약 세가지, 코칭은 제외!)
    @GetMapping("/{fileId}/details")
    public ResponseEntity<FileDetailResponseDto> getFileDetails(@PathVariable Long fileId) {
        return ResponseEntity.ok(textFileService.getFileDetails(fileId));
    }
}
