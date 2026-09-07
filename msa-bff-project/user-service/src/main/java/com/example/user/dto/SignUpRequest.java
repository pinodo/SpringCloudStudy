package com.example.user.dto;

public record SignUpRequest(
    String username,
    String password,
    String email,
    String nickname,
    String phoneNumber,
    String address
) {
}