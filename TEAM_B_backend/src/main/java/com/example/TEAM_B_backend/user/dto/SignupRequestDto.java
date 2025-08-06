package com.example.TEAM_B_backend.user.dto;

import lombok.Data;

@Data
public class SignupRequestDto {
    public String email;
    public String password;
    public String nickname;
}
