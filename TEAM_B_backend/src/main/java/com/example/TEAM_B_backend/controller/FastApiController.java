package com.example.TEAM_B_backend.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/fastapi")
public class FastApiController {

    @PostMapping("/upload")
    public ResponseEntity<?> uploadAndCallFastApi(@RequestParam("file") MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            return ResponseEntity.badRequest().body("파일이 비어 있습니다.");
        }

        try {
            // MultipartFile을 임시 파일로 저장
            File tempFile = File.createTempFile("upload-", multipartFile.getOriginalFilename());
            multipartFile.transferTo(tempFile);

            // FastAPI API 호출
            String fastApiUrl = "http://stt-server:8081/upload_stt_summary";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(tempFile));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(fastApiUrl, requestEntity,  String.class);

            // 임시 파일 삭제
            tempFile.delete();

            return ResponseEntity.ok(response.getBody());

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("파일 처리 중 오류 발생");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("FastAPI 요청 실패: " + e.getMessage());
        }
    }
}
