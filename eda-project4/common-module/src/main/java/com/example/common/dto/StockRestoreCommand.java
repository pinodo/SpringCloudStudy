package com.example.common.dto;

import java.time.Instant;

public record StockRestoreCommand(
  String orderId,
  String productId,
  Integer quantity,
  Instant createdAt
) { }
