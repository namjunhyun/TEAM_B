package com.example.TEAM_B_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.example.TEAM_B_backend.dto.SpeedRequestDto;
import com.example.TEAM_B_backend.dto.PauseRequestDto;


import java.io.File;

@Service
public class FastApiService {

    @Autowired
    private RestTemplate restTemplate;

    public String uploadAudioFileToFastApi(File audioFile) {
        String fastApiUrl = "http://stt-server:8000/upload_stt_summary";

        // 멀티파트 폼 데이터 준비
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        Resource fileResource = new FileSystemResource(audioFile);
        body.add("file", fileResource);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(fastApiUrl, requestEntity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();  // FastAPI가 JSON 문자열로 반환함
        } else {
            throw new RuntimeException("FastAPI 호출 실패: " + response.getStatusCode());
        }
    }

    // 속도 측정 서비스
    public String callAnalyzeSpeed(SpeedRequestDto dto) {
        String url = "http://stt-server:8000/analyze_speed";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SpeedRequestDto> request = new HttpEntity<>(dto, headers);
        return restTemplate.postForObject(url, request, String.class);
    }

    // 공백 측정 서비스
    public String callAnalyzePause(PauseRequestDto dto) {
        String url = "http://stt-server:8000/analyze_pause";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PauseRequestDto> request = new HttpEntity<>(dto, headers);
        return restTemplate.postForObject(url, request, String.class);
    }
}
