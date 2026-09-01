package com.example.product.controller;

import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
  
  private final Environment env;

  // 로드밸런싱 확인용 API
  @GetMapping("/port")
  public String getPort() {
    return "실행 중인 Product Service PORT: " + env.getProperty("local.server.port");
  }
}
