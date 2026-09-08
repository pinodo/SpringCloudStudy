package com.example.order.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.common.dto.OrderCreatedRequest;
import com.example.common.dto.event.OrderCreatedEvent;
import com.example.order.entity.Order;
import com.example.order.entity.OrderStatus;
import com.example.order.entity.Outbox;
import com.example.order.entity.OutboxStatus;
import com.example.order.repository.OrderRepository;
import com.example.order.repository.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final OutboxRepository outboxRepository;
  private final ObjectMapper objectMapper;

  /**
   * Outbox 패턴 적용 주문 생성
   * DB 저장과 Outbox 이벤트를 단일 로컬 트랜잭션(@Transactional)으로 보장함
   */
  @Transactional
  public String createOrderV1(OrderCreatedRequest request) {
    String orderId = UUID.randomUUID().toString();

    // 1. 주문 엔티티 생성 및 DB 저장 (PENDING)
    Order order = Order.builder()
        .orderId(orderId)
        .productId(request.productId())
        .quantity(request.quantity())
        .status(OrderStatus.PENDING)
        .build();
    orderRepository.save(order);

    // 2. 이벤트 페이로드 생성
    OrderCreatedEvent event = new OrderCreatedEvent(
        orderId,
        request.productId(),
        request.quantity(),
        Instant.now()
    );

    try {
      // 3. Outbox 엔티티 생성 및 DB 저장 (동일한 RDB 트랜잭션 범위)
      String jsonPayload = objectMapper.writeValueAsString(event);

      Outbox outbox = Outbox.builder()
          .id(UUID.randomUUID().toString())
          .aggregateType("ORDER")
          .aggregateId(orderId)
          .destination("order-created-topic")
          .type("OrderCreatedEvent")
          .payload(jsonPayload)
          .status(OutboxStatus.PENDING)
          .createdAt(Instant.now())
          .build();

      outboxRepository.save(outbox);
      log.info("[Outbox Pattern] Order and Outbox saved atomically. Order ID: {}", orderId);

    } catch (Exception e) {
      log.error("[Outbox Pattern Error] Failed to serialize event payload", e);
      throw new IllegalStateException("Order creation failed due to event serialization error", e);
    }

    return orderId;
  }

  @Transactional
  public void completeOrder(String orderId) {
    Order order = orderRepository.findById(orderId)
      .orElseThrow(() -> new RuntimeException("Order not found for completion: " + orderId));
    order.complete();  // COMPLETED 상태로 변경 (Dirty Checking)
    log.info("[Saga Completed] Order ID: {} -> COMPLETED", orderId);
  }

  @Transactional
  public void cancelOrder(String orderId) {
    Order order = orderRepository.findById(orderId)
      .orElseThrow(() -> new RuntimeException("Order not found for cancellation: " + orderId));
    order.cancel();  // CANCELLED 상태로 변경 (Dirty Checking)
    log.warn("[Saga Compensated] Order ID: {} -> CANCELLED", orderId);
  }
}