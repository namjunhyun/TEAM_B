package com.example.TEAM_B_backend.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        /*registry.addMapping("/**") // 모든 엔드포인트에 대해
                .allowedOriginPatterns("https://*.vercel.app") // Vercel 우리 프론트 주소에서 *로 수정
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true); // 필요한 경우 인증정보 허용
    */}
}
