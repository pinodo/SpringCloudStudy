package com.example.stock.consumer;

import java.time.Instant;
import java.util.function.Consumer;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.common.dto.event.OrderCreatedEvent;
import com.example.common.dto.event.StockFailedEvent;
import com.example.common.dto.event.StockReservedEvent;
import com.example.stock.service.StockService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class StockConsumer {

  private final StockService stockService;
  private final StreamBridge streamBridge;

  @Bean
  public Consumer<OrderCreatedEvent> stockControl() {
    return event -> {
      log.info("[Saga Step] OrderCreatedEvent Received: Order ID {}, Product {}", 
          event.orderId(), event.productId());

      try {
        // 비관적 락 기반 재고 차감 시도
        stockService.decreaseStock(event.productId(), event.quantity());

        StockReservedEvent reservedEvent = new StockReservedEvent(
            event.orderId(),
            event.productId(),
            event.quantity(),
            Instant.now()
        );
        // 성공 토픽으로 발송
        streamBridge.send("stockReserved-out-0", reservedEvent);
        log.info("[Event Published] StockReservedEvent -> stock-success-topic: {}", event.orderId());

      } catch (Exception e) {
        log.error("[Saga Step Failed] Reason: {}. Publishing StockFailedEvent", e.getMessage());

        StockFailedEvent failedEvent = new StockFailedEvent(
            event.orderId(),
            event.productId(),
            event.quantity(),
            e.getMessage(),
            Instant.now()
        );
        // 실패 토픽으로 발송
        streamBridge.send("stockFailed-out-0", failedEvent);
        log.warn("[Event Published] StockFailedEvent -> stock-failure-topic: {}", event.orderId());
      }
    };
  }
}