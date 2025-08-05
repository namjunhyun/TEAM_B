package com.example.TEAM_B_backend.core.service;

import com.example.TEAM_B_backend.core.dto.TextFileDto;
import com.example.TEAM_B_backend.core.dto.SpeedRequestDto;
import com.example.TEAM_B_backend.core.dto.PauseRequestDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;

@Service
public class FastApiService {

    private final RestTemplate restTemplate = new RestTemplate();

    public TextFileDto uploadAudioFileToFastApi(File audioFile) {
        String fastApiUrl = "http://stt-server:8000/upload_stt_summary";

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        Resource fileResource = new FileSystemResource(audioFile);
        body.add("file", fileResource);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                fastApiUrl,
                HttpMethod.POST,
                requestEntity,
                String.class // 👉 JSON 문자열로 받음
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                String responseBody = response.getBody();
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(responseBody);

                String transcript = root.path("original_text").asText();
                JsonNode summaries = root.path("summaries");

                String summary1 = summaries.path("간단요약").asText();
                String summary2 = summaries.path("상세요약").asText();
                String summary3 = summaries.path("키워드요약").asText();

                TextFileDto dto = new TextFileDto();
                dto.setTranscript(transcript);
                dto.setSummary1(summary1);
                dto.setSummary2(summary2);
                dto.setSummary3(summary3);

                return dto;
            } catch (Exception e) {
                throw new RuntimeException("FastAPI 응답 파싱 중 오류 발생", e);
            }
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
