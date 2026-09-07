package com.example.user.dto;

public record SignUpRequest(
    String username,    // Keycloak ID
    String password,    // Keycloak PW
    String email,       // Keycloak Email
    String nickname,    // User Service DB 프로필 데이터
    String phoneNumber, // User Service DB 프로필 데이터
    String address      // User Service DB 프로필 데이터
) {
  
}