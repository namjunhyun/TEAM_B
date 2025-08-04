package com.example.TEAM_B_backend.core.dto;

import lombok.Data;

import java.util.List;

@Data
public class PauseRequestDto {
    private String text;
    private List<SegmentDto> segments;
}