package com.example.user.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.common.dto.UserResponse;

import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("/api/users")
public class UserController {

  // DB 대용 Mock Data
  private final Map<Long, UserResponse> userMap = new HashMap<>();

  @PostConstruct
  public void init() {
    userMap.put(1L, new UserResponse(1L, "홍길동", "hong@test.com"));
    userMap.put(2L, new UserResponse(2L, "김철수", "kim@test.com"));
    userMap.put(3L, new UserResponse(3L, "이영희", "lee@test.com"));
  }

  @GetMapping("/{userId}")
  public ResponseEntity<UserResponse> getUser(@PathVariable("userId") Long userId) {
    // 존재하는 사용자 ID이면 정상 데이터 반환
    if (userMap.containsKey(userId)) {
      return ResponseEntity.ok(userMap.get(userId));
    }
    // 그 외는 404 반환
    // Consumer(board-service)의 OpenFeign ErrorDecoder가 이를 캐치함
    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");
  }
}