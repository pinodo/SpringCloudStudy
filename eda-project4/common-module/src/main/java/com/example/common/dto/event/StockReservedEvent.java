package com.example.common.dto.event;

import java.time.Instant;

public record StockReservedEvent(
    String orderId,
    String productId,
    Integer quantity,
    Instant occurredAt
) {
}