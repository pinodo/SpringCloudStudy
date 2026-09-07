package com.example.resource.dto;

public record UserProfileResponse(
    String userId,
    String username,
    String email,
    boolean emailVerified
) {
    
}