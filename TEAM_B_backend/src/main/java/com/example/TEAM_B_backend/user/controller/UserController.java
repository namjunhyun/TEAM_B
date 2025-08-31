package com.example.TEAM_B_backend.user.controller;

import com.example.TEAM_B_backend.user.dto.LoginRequestDto;
import com.example.TEAM_B_backend.user.dto.SignupRequestDto;
import com.example.TEAM_B_backend.user.entity.User;
import com.example.TEAM_B_backend.user.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequestDto dto) {
        try {
            userService.signup(dto);
            return ResponseEntity.ok("회원가입 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("서버 오류가 발생했습니다.");
        }
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto dto, HttpServletRequest request, HttpServletResponse response) {
        // ✅ 디버깅용 로그 출력
        System.out.println("💬 로그인 요청됨");
        System.out.println("💬 이메일: " + dto.getEmail());
        System.out.println("💬 비밀번호: " + dto.getPassword());
        System.out.println("💬 rememberMe: " + dto.isRememberMe());

        try {
            User user = userService.login(dto.getEmail(), dto.getPassword());
            HttpSession session = request.getSession();
            session.setAttribute("userId", user.getId());
            session.setAttribute("email", user.getEmail());
            session.setAttribute("nickname", user.getNickname());

            if (dto.isRememberMe()) {
                session.setMaxInactiveInterval(60 * 60 * 24 * 14); // 2주
            } else {
                session.setMaxInactiveInterval(60 * 60); // 1시간
            }

            // 스프링 시큐리티 인증 컨텍스트 구성
            var authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
            var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            // 세션에 SecurityContext 저장
            new HttpSessionSecurityContextRepository().saveContext(context, request, response);
            return ResponseEntity.ok("로그인 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("서버 오류가 발생했습니다.");
        }
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok("로그아웃 완료");
    }

    // 로그인 상태 확인
    @GetMapping("/me")
    public ResponseEntity<String> getLoginUser(HttpServletRequest request) {
//        try {
//            HttpSession session = request.getSession(false);
//            if (session == null || session.getAttribute("userId") == null) {
//                return ResponseEntity.status(401).body("로그인되지 않음");
//            }
//            String nickname = (String) session.getAttribute("nickname");
//            return ResponseEntity.ok("로그인 중: nickname=" + nickname);
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().body("서버 오류가 발생했습니다.");
//        }
        String nickname = (String) request.getSession().getAttribute("nickname");
        return ResponseEntity.ok("로그인 중: nickname=" + nickname);
    }

}
