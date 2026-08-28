package com.example.message.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class MessageController {

  // K8s ConfigMap에 정의된 'message.greeting' 값을 가져옴
  // 만약 값이 없으면 기본값 "Default Hello"를 사용 (에러 방지용)
  @Value("${message.greeting:Default Hello}")
  private String greeting;

  @GetMapping("/message")
  public String message() {
    // 현재 설정된 인사말 반환
    return greeting + " (from K8s ConfigMap)";
  }
}