package com.example.summary_poc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenAiRequest {
    private String model; // gpt-4o-mini
    private List<Message> messages;
    private double temperature;

    @Data
    @AllArgsConstructor
    public static class Message {
        private String role; // user or system
        private String content; //메시지 본문
    }
}
