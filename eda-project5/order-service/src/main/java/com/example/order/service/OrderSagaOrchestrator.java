package com.example.order.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.common.dto.OrderCreatedRequest;
import com.example.common.dto.StockDeductCommand;
import com.example.common.dto.StockDeductedReply;
import com.example.common.dto.StockRestoreCommand;
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
public class OrderSagaOrchestrator {

  private final OrderRepository orderRepository;
  private final OutboxRepository outboxRepository;
  private final ObjectMapper objectMapper;

  /**
   * 1. 주문 생성 과정
   *    1) 주문 PENDING으로 생성
   *    2) 재고 차감 명령 실행
   */
  @Transactional
  public String createOrderV2(OrderCreatedRequest request) {
    String orderId = UUID.randomUUID().toString();

    // 1. 주문을 PENDING 상태로 DB에 저장
    Order order = Order.builder()
        .orderId(orderId)
        .productId(request.productId())
        .quantity(request.quantity())
        .status(OrderStatus.PENDING)
        .build();
    orderRepository.save(order);
    log.info("[Orchestrator Step 1] Order saved as PENDING. Order ID: {}", orderId);
    
    // 2. 재고 차감 명령 발행
    StockDeductCommand command = new StockDeductCommand(
      orderId,
      request.productId(),
      request.quantity(),
      Instant.now()
    );

    // 3. Outbox 테이블에 재고 차감 명령 저장 -> Debezium이 라우팅
    saveOutboxEvent(
      orderId,
      "stock-deduct-command-topic",
      "StockDeductCommand",
      command);
    log.info("[Orchestrator Step 1] Outbox 저장 완료(StockDeductCommand). Order ID: {}", orderId);

    return orderId;
  }

  /**
   * 2. 재고 차감 결과 수신 후 처리
   *    1) 재고 차감 실패 시: 주문 마무리 (CANCELLED)
   *    2) 재고 차감 성공 시: 이후 단계인 결제 진행
   *        (1) 결제 성공 시: 주문 마무리 (COMPLETED)
   *        (2) 결제 실패 시: 재고 보상 명령 발행 (StockRestoreCommand)
   */
  @Transactional
  public void handleStockReply(StockDeductedReply reply) {

    // find 결과는 영속성 컨텍스트에 저장(Managed Entity) -> Dirty Checking 가능
    Order order = orderRepository.findById(reply.orderId())
        .orElseThrow(() -> new IllegalArgumentException("Order not found. Order ID: " + reply.orderId()));
        
    // 재고 차감 실패 (주문 마무리: CANCELLED)
    if (!reply.success()) { 
      order.cancel(); // Dirty Checking으로 CANCELLED 처리 완료
      return;
    }

    // 재고 차감 성공 영역

    // 결제 시도 (동기 통신으로 구현: OpenFeign)
    boolean paymentSuccess = payment(order);

    // 결제 성공 시 (주문 마무리: COMPLETED)
    if (paymentSuccess) {
      order.complete(); // Dirty Checking으로 COMPLETED 처리 완료
      return;
    }
    // 결제 실패 시 (재고 보상 명령 발행)
    else {
      StockRestoreCommand stockRestoreCommand = new StockRestoreCommand(
        order.getOrderId(),
        order.getProductId(),
        order.getQuantity(), // 주문 당시 수량 -> 차감되어 있는 수량 -> 다시 복구해야 할 수량
        Instant.now());

      saveOutboxEvent(
        order.getOrderId(), 
        "stock-restore-command-topic", 
        "StockRestoreCommand", 
        stockRestoreCommand);
      log.warn("[Orchestrator Compensation] Outbox 저장 완료(StockRestoreCommand). Order ID: {}", order.getOrderId());

      order.cancel();
      log.warn("[Orchestrator Cancelled] 주문 취소. Order ID: {} -> CANCELLED", order.getOrderId());
    }
  }

  /**
   * Outbox 테이블 저장 메서드
   * - DB의 NOT NULL 제약조건을 만족하기 위해 CDC에선 불필요한 status와 createdAt 명시
   * - 현재 Outbox는 Polling과 CDC 모두 처리하기 위해 불필요한 필드가 섞여 있음
   */
  private void saveOutboxEvent(String aggregateId, String destination, String type, Object payloadObj) {
    try {
      String jsonPayload = objectMapper.writeValueAsString(payloadObj);

      Outbox outbox = Outbox.builder()
          .id(UUID.randomUUID().toString())
          .aggregateType("ORDER")
          .aggregateId(aggregateId)
          .destination(destination) // Debezium EventRouter 라우팅 대상 토픽
          .type(type)
          .payload(jsonPayload)
          .status(OutboxStatus.PENDING) // 통합 스키마 NOT NULL 방지 (CDC는 주로 payload/destination 기반 라우팅)
          .createdAt(Instant.now())
          .build();

      outboxRepository.save(outbox);
    } catch (Exception e) {
      log.error("[Jackson Error] {} 타입으로 직렬화 실패.", type, e);
      throw new RuntimeException("직렬화 실패로 Outbox 저장 실패", e);
    }
  }

  // 결제용 내부 메서드
  // 5개 이상 수량 요청 시 결제 실패
  private boolean payment(Order order) {
    return !(order.getQuantity() >= 5);
  }
}

