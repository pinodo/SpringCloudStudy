package com.example.order.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.common.dto.OrderCreatedRequest;
import com.example.common.dto.StockDeductCommand;
import com.example.common.dto.StockDeductedReply;
import com.example.common.dto.StockRestoreCommand;
import com.example.order.entity.Order;
import com.example.order.entity.OrderStatus;
import com.example.order.repository.OrderRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderSagaOrchestrator {

  private final OrderRepository orderRepository;
  private final StreamBridge streamBridge;

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
    streamBridge.send("deductStock-out-0", command);
    log.info("[Orchestrator Step 2] Sent StockDeductCommand -> RabbitMQ. Order ID: {}", orderId);

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

      streamBridge.send("restoreStock-out-0", stockRestoreCommand);
      order.cancel();
    }
  }

  // 결제용 내부 메서드
  // 5개 이상 수량 요청 시 결제 실패
  private boolean payment(Order order) {
    return !(order.getQuantity() >= 5);
  }
}

