package com.example.common.dto.event;

import java.time.Instant;

public record OrderCreatedEvent(
    String orderId,
    String productId,
    Integer quantity,
    Instant occurredAt
) {
}