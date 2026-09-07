package com.example.product.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {
  @GetMapping
  public String getProducts(@AuthenticationPrincipal Jwt jwt) {
    return "Product List (User: " + jwt.getClaimAsString("preferred_username") + ")";
  }
}