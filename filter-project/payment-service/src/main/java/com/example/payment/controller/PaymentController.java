package com.example.payment.controller;

import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {
  
  private final Environment env;

  @GetMapping("/info")
  public String getInfo() {
    return "Processing payment on PORT: " + env.getProperty("local.server.port");
  }
}
