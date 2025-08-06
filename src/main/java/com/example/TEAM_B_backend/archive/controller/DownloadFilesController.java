package com.example.TEAM_B_backend.archive.controller;

import com.example.TEAM_B_backend.archive.fileService.TextFileService;
import com.example.TEAM_B_backend.archive.dto.TextFileDto;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;


import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/archive")
public class DownloadFilesController {

    private final TextFileService textFileService;

    public DownloadFilesController(TextFileService textFileService) {
        this.textFileService = textFileService;
    }

    @GetMapping("/{fileId}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId,
                                                 @RequestParam Long userId) {
        // DB에서 파일과 사용자 정보로 파일 DTO 조회 및 권한 체크
        TextFileDto dto = textFileService.getTextFileByIdAndUserId(fileId, userId);

        // 텍스트 내용 조합
        StringBuilder sb = new StringBuilder();
        sb.append("=== Transcript ===\n").append(dto.getTranscript()).append("\n\n");
        sb.append("=== Summary 1 ===\n").append(dto.getSummary1()).append("\n\n");
        sb.append("=== Summary 2 ===\n").append(dto.getSummary2()).append("\n\n");
        sb.append("=== Keywords (Summary 3) ===\n").append(dto.getSummary3()).append("\n");

        // UTF-8 바이트 변환 및 리소스 생성
        byte[] data = sb.toString().getBytes(StandardCharsets.UTF_8);
        ByteArrayResource resource = new ByteArrayResource(data);

        // 파일 이름 생성 (공백은 _로 변환)
        String filename = (dto.getOriginalFileName() != null && !dto.getOriginalFileName().isEmpty())
                ? dto.getOriginalFileName().replaceAll("\\s+", "_") + "_summary.txt"
                : "summary.txt";

        // ResponseEntity로 파일 다운로드 응답 반환
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentLength(data.length)
                .contentType(MediaType.TEXT_PLAIN)
                .body(resource);
    }
}
