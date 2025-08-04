package com.example.TEAM_B_backend.user.dto;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String email;
    private String password;
    private boolean rememberMe;
}
