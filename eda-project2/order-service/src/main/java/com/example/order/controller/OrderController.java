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

  @PostMapping("/v2")
  public ResponseEntity<String> createOrderV2(@RequestBody OrderCreatedRequest request) {
    String orderId = orderService.createOrderV2(request);
    return ResponseEntity.ok("[v2] Order Created: " + orderId);
  }
}