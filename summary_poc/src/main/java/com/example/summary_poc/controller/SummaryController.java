package com.example.summary_poc.controller;

import com.example.summary_poc.dto.SummaryRequest;
import com.example.summary_poc.service.SummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/summary")
public class SummaryController {

    private final SummaryService summaryService;

    @PostMapping("/full")
    public ResponseEntity<Map<String, String>> uploadAndSummarize(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "3") int count
    ) throws IOException {
        // 1. 파일 저장 (임시)
        File tempFile = File.createTempFile("upload-", ".wav");
        file.transferTo(tempFile);

        // 2. STT 호출 (CLOVA API)
        String text = summaryService.transcribeWithClova(tempFile);

        // 3. 요약
        String summary = summaryService.extractSummary(text, count);

        // 4. 결과 반환
        Map<String, String> result = new HashMap<>();
        result.put("original", text);
        result.put("summary", summary);
        return ResponseEntity.ok(result);
    }
}
