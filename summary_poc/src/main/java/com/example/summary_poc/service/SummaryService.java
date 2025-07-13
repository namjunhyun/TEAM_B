package com.example.summary_poc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SummaryService {

    private final RestTemplate restTemplate;

    public String extractSummary(String text, int topN) {
        String[] sentences = text.split("(?<=[.!?])\\s+");

        Map<String, Integer> wordFreq = new HashMap<>();
        for (String sentence : sentences) {
            for (String word : sentence.toLowerCase().replaceAll("[^a-zA-Z가-힣\\s]", "").split("\\s+")) {
                if (word.length() > 1) {
                    wordFreq.put(word, wordFreq.getOrDefault(word, 0) + 1);
                }
            }
        }

        Map<String, Integer> sentenceScores = new HashMap<>();
        for (String sentence : sentences) {
            int score = 0;
            for (String word : sentence.toLowerCase().split("\\s+")) {
                score += wordFreq.getOrDefault(word, 0);
            }
            sentenceScores.put(sentence, score);
        }

        return sentenceScores.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(topN)
                .map(Map.Entry::getKey)
                .reduce("", (a, b) -> a + " " + b);
    }

    public String transcribeWithClova(File file) throws IOException {
        // Clova Speech API 파라미터
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("language", "ko-KR");
        params.put("completion", "sync");
        params.put("wordAlignment", true);
        params.put("fullText", true);

        // JSON -> HttpEntity (params 파트)
        HttpHeaders jsonHeaders = new HttpHeaders();
        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
        String jsonParams = new ObjectMapper().writeValueAsString(params);
        HttpEntity<String> paramsEntity = new HttpEntity<>(jsonParams, jsonHeaders);

        // Multipart 본문 구성
        MultiValueMap<String, Object> multipartBody = new LinkedMultiValueMap<>();
        multipartBody.add("media", new FileSystemResource(file));
        multipartBody.add("params", paramsEntity);

        // 헤더 설정 (Content-Type 생략, Spring이 자동으로 multipart 설정)
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-CLOVASPEECH-API-KEY", "c1b443db535f41e184ea5e232bd8a833");

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(multipartBody, headers);

        String url = "https://clovaspeech-gw.ncloud.com/external/v1/12092/bdc2926aa7c7d85cca136a11511000fd77f3d720db7e9c83d636bf5c370b5032/recognizer/upload";

        ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
        Map result = response.getBody();

        System.out.println("CLOVA 응답: " + result);

        if (result == null || result.containsKey("error")) {
            throw new RuntimeException("Clova 오류: " + result);
        }

        Object text = result.get("text");
        return text != null ? text.toString() : null;
    }


}
