package com.example.TEAM_B_backend.core.dto;

import lombok.Data;

@Data
public class TextFileDto {
    private String transcript;
    private String summary1;
    private String summary2;
    private String summary3;
    private Long userId;
}
