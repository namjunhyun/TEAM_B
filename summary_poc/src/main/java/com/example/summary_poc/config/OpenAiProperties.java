package com.example.summary_poc.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "openai.api")
@Getter
@EnableConfigurationProperties(OpenAiProperties.class)
public class OpenAiProperties {
    private String key;

    public void setKey(String key) {
        this.key = key;
    }
}

