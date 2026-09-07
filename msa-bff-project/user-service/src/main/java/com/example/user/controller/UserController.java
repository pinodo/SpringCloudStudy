package com.example.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.user.dto.SignUpRequest;
import com.example.user.dto.UserProfileResponse;
import com.example.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping("/signup")
  public ResponseEntity<String> signUp(@RequestBody SignUpRequest request) {
    userService.signUp(request);
    return ResponseEntity.status(HttpStatus.CREATED).body("회원 가입 완료");
  }

  @GetMapping("/me")
  public ResponseEntity<UserProfileResponse> getMyProfile(@AuthenticationPrincipal Jwt jwt) {
    String keycloakUserId = jwt.getSubject();
    String username = jwt.getClaimAsString("preferred_username");
    String email = jwt.getClaimAsString("email");

    UserProfileResponse profile = userService.getMyProfile(keycloakUserId, username, email);
    return ResponseEntity.ok(profile);
  }
}