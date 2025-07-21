package com.example.TEAM_B_backend.controller;

import com.example.TEAM_B_backend.service.AudioStorageService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/files/upload")
public class AudioUploadController {

    @Autowired
    private AudioStorageService storageService;

    @PostConstruct
    public void init() {
        System.out.println("✅ AudioUploadController 초기화됨");
    }

    @PostMapping("/audio")
    public ResponseEntity<?> uploadAudio(@RequestParam("file") MultipartFile file) {

        System.out.println("업로드된 파일 Content-Type: " + file.getContentType());

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "파일이 비어 있습니다."));
        }

        String contentType = file.getContentType();
        if (contentType == null ||
                (!contentType.equals("audio/mpeg") &&
                        !contentType.equals("audio/wav") &&
                        !contentType.equals("audio/wave") &&
                        !contentType.equals("audio/x-wav") &&
                        !contentType.equals("audio/x-m4a") &&
                        !contentType.equals("audio/m4a") &&
                        !contentType.equals("audio/mp4") &&
                        !contentType.equals("audio/aac"))) {
            return ResponseEntity.badRequest().body(Map.of("error", "지원하지 않는 파일 형식입니다."));
        }

        try {
            String fileId = storageService.store(file);
            return ResponseEntity.ok(Map.of("file_id", fileId, "status", "processing"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "파일 저장 실패"));
        }
    }

}
