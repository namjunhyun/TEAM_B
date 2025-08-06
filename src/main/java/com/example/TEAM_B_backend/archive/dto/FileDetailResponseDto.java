package com.example.TEAM_B_backend.archive.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
public class FileDetailResponseDto {
    private String originalText;
    private Map<String, String> summaries;

    public FileDetailResponseDto(String originalText, Map<String, String> summaries) {
        this.originalText = originalText;
        this.summaries = summaries;
    }
}
