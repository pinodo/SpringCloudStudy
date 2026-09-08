package com.example.common.dto;

import java.time.Instant;

public record StockDeductedReply(
  String orderId,
  boolean success,
  String reason,
  Instant repliedAt
) { }
