package com.example.TEAM_B_backend.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity // Spring Security를 활성화하는 어노테이션
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. CORS 설정 : CorsConfigurationSource 빈을 사용하여 CORS 정책을 적용
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Security 레벨에서 CORS 활성화
                // 2. CSRF 비활성화 : REST API에서는 CSRF 토큰을 사용하지 않으므로 비활성화
                .csrf(cs -> cs.disable()) // 버전에 따라 방식 변경
                // 3. 인가(Authorization) 설정 : 어떤 요청에 접근을 허용할지 정의
                .authorizeHttpRequests(auth -> auth
                        // OPTIONS 메서드는 프리플라이트 요청이므로 전부 허용
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 로그인 및 회언가입, 비밀번호 찾기 경로는 인증 없이 접근을 허용
                        .requestMatchers(HttpMethod.POST,
                                "/api/user/login",
                                "/api/user/signup",
                                "/api/user/request-reset",
                                "api/user/reset-password"
                                ).permitAll() // 로그인/인증 공개
                        // 그 외 모든 요청은 반드시 인증된 사용자만 접근할 수 있도록 설정
                        .anyRequest().authenticated() // 모든 요청 허용 pemitAll() -> authenticated() 인증필요
                );
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowCredentials(true); //쿠키/자격증명 포함 허용
        cfg.setAllowedOriginPatterns(List.of(
                "https://*.vercel.app",
                "https://saymary.site",
                "https://*.saymary.site",
                "http://localhost:*",
                "https://localhost:*"
        ));
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        //프론트에서 읽어야 하는 헤더가 있으면 노출
        cfg.setExposedHeaders(List.of("Authorization", "Set-Cookie"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);

        // 확인용 로그
        System.out.println("CORS origins = " + cfg.getAllowedOriginPatterns());
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
