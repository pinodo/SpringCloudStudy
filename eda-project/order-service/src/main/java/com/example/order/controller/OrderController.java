package com.example.order.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.common.dto.OrderCreatedRequest;
import com.example.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
  
  private final OrderService orderService;

  @PostMapping("/v1")
  public ResponseEntity<String> createOrderV1(@RequestBody OrderCreatedRequest request) {
    String orderId = orderService.createOrderV1(request);
    return ResponseEntity.ok("[v1] Order Created: " + orderId);
  }
}