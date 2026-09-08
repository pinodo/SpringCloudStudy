package com.example.common.dto;

import java.time.Instant;

public record StockDeductCommand(
  String orderId,
  String productId,
  Integer quantity,
  Instant createdAt
) { }
