package com.example.user.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RefreshScope
public class UserController {

  private final Environment env;

  @Value("${user.welcome.message:기본메시지}")
  private String message;

  @GetMapping("/message")
  public String message() {
    String currentPort = env.getProperty("local.server.port");
    return String.format("User Service [Port %s]: %s", currentPort, message);
  }
}
