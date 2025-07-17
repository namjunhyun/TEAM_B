package com.example.summary_poc.service;

import com.example.summary_poc.config.ClovaProperties;
import com.example.summary_poc.config.OpenAiProperties;
import com.example.summary_poc.dto.OpenAiRequest;
import com.example.summary_poc.dto.OpenAiResponse;
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
    private final OpenAiProperties openAiProperties; // 둘 다 openai api를 연동기 시키기 위해 주입
    private final ClovaProperties clovaProperties; // clova-speech

    // opneai api url과 model
    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "gpt-4o-mini";

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
        // 키 가리기 용도
        String clovaKey = clovaProperties.getKey();

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
        headers.set("X-CLOVASPEECH-API-KEY", clovaKey);

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

    // 여기부터 openai api를 이용한 요약 기능 함수들
    // 3줄요약
    public String summarizeIn3Lines(String originalText) {
        System.out.println("OPENAI_KEY: " + openAiProperties.getKey()); // 키 점검 차원
        return callOpenAi("다음 글을 세 줄로 요약해줘:\n\n" + originalText, 0.5);
    }

    // 핵심 키워드
    public String extractKeywords(String originalText) {
        return callOpenAi("다음 글에서 핵심 키워드 5~10개를 추출해서 쉼표로 구분해줘:\n\n" + originalText, 0.3);
    }

    public String callOpenAi(String prompt, double temperature) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiProperties.getKey());

        OpenAiRequest request = new OpenAiRequest(
                MODEL,
                List.of(
                        new OpenAiRequest.Message("system", "너는 문서 요약 전문가야."),
                        new OpenAiRequest.Message("user", prompt)
                ),
                temperature
        );

        HttpEntity<OpenAiRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<OpenAiResponse> response = restTemplate.postForEntity(
                OPENAI_URL, entity, OpenAiResponse.class
        );

        // 에러 코드 구현
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody().getChoices().get(0).getMessage().getContent().trim();
        } else {
            throw new RuntimeException("OpenAI API 호출 실패: " + response.getStatusCode());
        }
    }
}
