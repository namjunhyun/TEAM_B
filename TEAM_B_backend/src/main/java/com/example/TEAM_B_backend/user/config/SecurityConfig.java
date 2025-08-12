package com.example.TEAM_B_backend.user.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults()) // Security 레벨에서 CORS 활성화
                .csrf(csrf -> csrf.disable()) // 버전에 따라 방식 변경
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // 프리플라이트 전부 허용
                        .requestMatchers(HttpMethod.POST,"/api/user/login","/api/user/signup").permitAll() // 로그인/인증 공개
                        .anyRequest().authenticated() // 모든 요청 허용 -> authenticated() 인증필요
                );
//                .formLogin(form -> form
//                    .loginProcessingUrl("/login") // 로그인 요청을 처리할 URL
//                        .permitAll()
//                );
        return http.build();
    }

//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration cfg = new CorsConfiguration();
//        cfg.setAllowCredentials(true); //쿠키/자격증명 포함 허용
//        cfg.setAllowedOrigins(List.of("https://saymary.site"));
//        // 혼선 방지로 주석 처리
//        cfg.setAllowedOriginPatterns(List.of(
//                "https://*.vercel.app"
//        ));
//        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        cfg.setAllowedHeaders(List.of("*"));
//        //프론트에서 읽어야 하는 헤더가 있으면 노출
//        cfg.setExposedHeaders(List.of("Authorization", "Set-Cookie"));
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", cfg);
//        return source;
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
