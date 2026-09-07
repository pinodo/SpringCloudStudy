package com.example.resource.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.resource.dto.UserProfileResponse;

@RestController
@RequestMapping("/api/users")
public class UserController {

  @GetMapping("/me")
  public ResponseEntity<UserProfileResponse> getMyProfile(@AuthenticationPrincipal Jwt jwt) {
    String userId = jwt.getSubject();
    String username = jwt.getClaimAsString("preferred_username");
    String email = jwt.getClaimAsString("email");
    Boolean emailVerified = jwt.getClaimAsBoolean("email_verified");

    UserProfileResponse response = new UserProfileResponse(
        userId,
        username,
        email,
        emailVerified != null && emailVerified
    );
    return ResponseEntity.ok(response);
  }
}