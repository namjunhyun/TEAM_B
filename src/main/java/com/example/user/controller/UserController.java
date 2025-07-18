package com.example.user.controller;

import com.example.user.dto.LoginRequestDto;
import com.example.user.dto.SignupRequestDto;
import com.example.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    public final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequestDto dto) {
        userService.signup(dto); //userService에 signup 기능 필요
        return ResponseEntity.ok("회원가입 성공");
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto dto, HttpServletRequest request) {
        User user = userService.login(dto.getEmail(), dto.getPassword()); //userService에 login 기능 필요
        HttpSession session = request.getSession(); // 사용자를 위한 로그인 공간 준비
        session.setAttribute("userId", user.getId()); // 세션 공간에 userId라는 이름으로 사용자 ID저장
        return ResponseEntity.ok("로그인 성공");
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // false: 없으면 null 반환
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok("로그아웃 완료");
    }

    // 로그인 상태 확인
    // 백엔드 - 로그인 X -> 로그인 기능 거절, 프론트 -> 사용자 에게 상태 제공 도움
    @GetMapping("/me")
    public ResponseEntity<String> getLoginUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            return ResponseEntity.status(401).body("로그인되지 않음");
        }
        Long userId = (Long) session.getAttribute("userId");
        return ResponseEntity.ok("로그인 중: userId=" + userId);
    }
}
