package com.example.TEAM_B_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication(scanBasePackages = "com.example.TEAM_B_backend")
@Import(SecurityConfig.class)
public class TEAM_B_backendApplication {

    public static void main(String[] args) {
        SpringApplication.run(TEAM_B_backendApplication.class, args);
    }

}
