package com.example.TEAM_B_backend.controller;

import com.example.TEAM_B_backend.service.FlaskApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/flask")
public class FlaskController {

    @Autowired
    private FlaskApiService flaskApiService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadAndCallFlask(@RequestParam("file") MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            return ResponseEntity.badRequest().body("파일이 비어 있습니다.");
        }

        try {
            // MultipartFile을 임시 파일로 저장
            File tempFile = File.createTempFile("upload-", multipartFile.getOriginalFilename());
            multipartFile.transferTo(tempFile);

            // Flask API 호출
            String flaskResponse = flaskApiService.uploadAudioFileToFlask(tempFile);

            // 임시 파일 삭제
            tempFile.delete();

            return ResponseEntity.ok(flaskResponse);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("파일 처리 중 오류 발생");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
