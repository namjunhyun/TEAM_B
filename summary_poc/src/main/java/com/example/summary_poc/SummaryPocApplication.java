package com.example.summary_poc;

import com.example.summary_poc.config.ClovaProperties;
import com.example.summary_poc.config.OpenAiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
        OpenAiProperties.class,
        ClovaProperties.class
})
public class SummaryPocApplication {

    public static void main(String[] args) {
        SpringApplication.run(SummaryPocApplication.class, args);
    }

}
