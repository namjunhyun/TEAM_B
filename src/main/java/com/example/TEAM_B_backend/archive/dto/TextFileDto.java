package com.example.TEAM_B_backend.archive.dto;

import com.example.TEAM_B_backend.archive.entity.TextFile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TextFileDto {
    private String originalFileName; // 원문
    private String transcript; // STT 변환문
    private String summary1; // 일반 요약
    private String summary2; // 핵심 문장 요약
    private String summary3; // 한문장으로 짧게 요약
    private Long userId;
    private Long fileId;

    public TextFileDto(TextFile textFile) {
        this.originalFileName = textFile.getOriginalFileName();
        this.transcript = textFile.getTranscript();
        this.summary1 = textFile.getSummary1();
        this.summary2 = textFile.getSummary2();
        this.summary3 = textFile.getSummary3();
        this.userId = textFile.getUser().getId();
        this.fileId = textFile.getId();
    }
}
