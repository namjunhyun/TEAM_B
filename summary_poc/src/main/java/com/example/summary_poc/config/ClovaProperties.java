package com.example.summary_poc.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@ConfigurationProperties(prefix = "clova.api")
public class ClovaProperties {
    private String key;

    public void setKey(String key){
        this.key = key;
    }
}
