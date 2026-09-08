package com.example.common.dto.event;

import java.time.Instant;

public record StockFailedEvent(
    String orderId,
    String productId,
    Integer quantity,
    String reason,
    Instant occurredAt
) {
}