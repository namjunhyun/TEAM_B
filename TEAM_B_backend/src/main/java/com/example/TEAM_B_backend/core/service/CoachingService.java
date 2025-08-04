package com.example.TEAM_B_backend.core.service;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;

@Service
public class CoachingService {

    private final RestTemplate restTemplate = new RestTemplate();

    public String sendAudioWithContextToFastApi(File audioFile,
                                                String situation,
                                                String audience,
                                                String style) {

        String fastApiUrl = "http://stt-server:8000/upload_feedback";

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        Resource fileResource = new FileSystemResource(audioFile);
        body.add("file", fileResource);
        body.add("situation", situation);
        body.add("audience", audience);
        body.add("style", style);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(fastApiUrl, requestEntity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody(); // FastAPI가 JSON 문자열로 반환
        } else {
            throw new RuntimeException("FastAPI 호출 실패: " + response.getStatusCode());
        }
    }
}
