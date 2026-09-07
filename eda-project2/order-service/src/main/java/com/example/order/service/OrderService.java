package com.example.order.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.common.dto.OrderCreatedRequest;
import com.example.common.dto.event.OrderCreatedEvent;
import com.example.order.entity.Order;
import com.example.order.entity.OrderStatus;
import com.example.order.repository.OrderRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  public String createOrderV2(OrderCreatedRequest request) {
    String orderId = UUID.randomUUID().toString();

    Order order = Order.builder()
        .orderId(orderId)
        .productId(request.productId())
        .quantity(request.quantity())
        .status(OrderStatus.PENDING)
        .build();

    orderRepository.save(order);
    log.info("[Saga Started] Order Created in PENDING. Order ID: {}", orderId);

    // After-Commit 리스너가 RabbitMQ로 발행하도록 내부 이벤트 게시
    eventPublisher.publishEvent(new OrderCreatedEvent(
        orderId,
        request.productId(),
        request.quantity(),
        Instant.now()
    ));

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