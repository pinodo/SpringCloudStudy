package com.example.order.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.common.dto.OrderCreatedRequest;
import com.example.common.dto.event.OrderCreatedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  public String createOrderV1(OrderCreatedRequest request) {
    // 주문 아이디: UUID
    String orderId = UUID.randomUUID().toString();

    // 주문 저장 (DB Insert 가정)
    log.info("[Order Saved] ID: {}", orderId);

    // 이벤트 메시지 생성
    OrderCreatedEvent event = new OrderCreatedEvent(
        orderId,
        request.productId(),
        request.quantity(),
        Instant.now()
    );
    
    // 이벤트 발행 (트랜잭션 완료 이전)
    // ApplicationContext 내부 이벤트 버스로 OrderCreatedEvent가 발행
    eventPublisher.publishEvent(event);

    return orderId;
  }
}