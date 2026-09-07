package com.example.user.dto;

public record UserProfileResponse(
    String userId,
    String username,
    String email,
    String nickname,
    String phoneNumber,
    String address
) {
}