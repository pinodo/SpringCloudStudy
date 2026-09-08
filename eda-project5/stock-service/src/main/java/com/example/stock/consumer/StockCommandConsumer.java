package com.example.stock.consumer;

import java.time.Instant;
import java.util.function.Consumer;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.common.dto.StockDeductCommand;
import com.example.common.dto.StockDeductedReply;
import com.example.common.dto.StockRestoreCommand;
import com.example.stock.service.StockService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class StockCommandConsumer {

  private final StockService stockService;
  private final StreamBridge streamBridge;

  /**
   * 1. [V2 차감 명령 수신] 재고 차감 실행 후 성공/실패 여부를 담은 Reply 전송
   */
  @Bean
  public Consumer<StockDeductCommand> processStockDeductCommand() {
    return cmd -> {
      log.info("[Command Inbound] StockDeductCommand Received. Order ID: {}, Product: {}, Quantity: {}", 
          cmd.orderId(), cmd.productId(), cmd.quantity());

      try {
        // 비관적 락 기반 재고 차감 시도
        stockService.decreaseStock(cmd.productId(), cmd.quantity());

        // 성공 Reply 전송
        StockDeductedReply successReply = new StockDeductedReply(
            cmd.orderId(),
            true,
            null,
            Instant.now()
        );
        streamBridge.send("stockReply-out-0", successReply);
        log.info("[Reply Outbound] Sent StockDeductedReply (SUCCESS) -> RabbitMQ. Order ID: {}", cmd.orderId());

      } catch (Exception e) {
        log.error("[Command Execution Failed] Deduction failed: {}", e.getMessage());

        // 실패 Reply 전송 (재고 부족 등)
        StockDeductedReply failReply = new StockDeductedReply(
            cmd.orderId(),
            false,
            e.getMessage(),
            Instant.now()
        );
        streamBridge.send("stockReply-out-0", failReply);
        log.warn("[Reply Outbound] Sent StockDeductedReply (FAILED) -> RabbitMQ. Order ID: {}", cmd.orderId());
      }
    };
  }

  /**
   * 2. [V2 보상 명령 수신] 오케스트레이터의 결제 실패 보상 명령을 받아 재고 원상복구
   */
  @Bean
  public Consumer<StockRestoreCommand> processStockRestoreCommand() {
    return cmd -> {
      log.warn("[Compensating Command Inbound] StockRestoreCommand Received for Order ID: {}. Restoring product: {}, qty: {}", 
          cmd.orderId(), cmd.productId(), cmd.quantity());

      try {
        stockService.restoreStock(cmd.productId(), cmd.quantity());
        log.warn("[Compensation Completed] Stock successfully rolled back for Order ID: {}", cmd.orderId());
      } catch (Exception e) {
        log.error("[Compensation CRITICAL Failure] Failed to restore stock for Order ID: {}. Reason: {}", 
            cmd.orderId(), e.getMessage());
      }
    };
  }
}