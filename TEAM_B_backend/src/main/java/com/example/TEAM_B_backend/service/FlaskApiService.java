package com.example.TEAM_B_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;

@Service
public class FlaskApiService {

    @Autowired
    private RestTemplate restTemplate;

    public String uploadAudioFileToFlask(File audioFile) {
        String flaskUrl = "http://stt-server:5000/upload_stt_summary";




        // 멀티파트 폼 데이터 준비
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        Resource fileResource = new FileSystemResource(audioFile);
        body.add("file", fileResource);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(flaskUrl, requestEntity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();  // Flask가 JSON 문자열로 반환함
        } else {
            throw new RuntimeException("Flask API 호출 실패: " + response.getStatusCode());
        }
    }
}
