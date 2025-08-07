package com.example.TEAM_B_backend.user.controller;

import com.example.TEAM_B_backend.user.dto.LoginRequestDto;
import com.example.TEAM_B_backend.user.dto.SignupRequestDto;
import com.example.TEAM_B_backend.user.entity.User;
import com.example.TEAM_B_backend.user.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public ResponseEntity<String> login(@RequestBody LoginRequestDto dto, HttpServletRequest request) {
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

    // 로그인 상태 확인 (JSON 반환)
    @GetMapping("/me")
    public ResponseEntity<?> getLoginUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인되지 않음"));
        }
        Long userId = (Long) session.getAttribute("userId");
        String email = (String) session.getAttribute("email");
        String nickname = (String) session.getAttribute("nickname");

        return ResponseEntity.ok(
                Map.of(
                        "userId", userId,
                        "email", email,
                        "nickname", nickname
                )
        );
    }
}
