package com.example.TEAM_B_backend;

import com.example.TEAM_B_backend.user.config.SecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootApplication(scanBasePackages = "com.example.TEAM_B_backend")
public class TEAM_B_backendApplication {

    public static void main(String[] args) {
        SpringApplication.run(TEAM_B_backendApplication.class, args);
    }

}
