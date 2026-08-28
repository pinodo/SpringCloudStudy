package com.example.board.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.common.dto.UserResponse;

// 인터페이스 + 어노테이션 방식의 선언적 HTTP 클라이언트 방식

@FeignClient(name = "user-service")  // name: 호출 대상이 되는 유레카 서비스 논리 식별 ID값 연동
public interface UserClient {

  // 호출하려는 API 명세와 정확히 일치시켜야 함
  @GetMapping("/api/users/{userId}")
  UserResponse getUser(@PathVariable("userId") Long userId);
}