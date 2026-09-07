package com.example.stock.consumer;

import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.common.dto.event.OrderCreatedEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class StockConsumer {

  @Bean
  public Consumer<OrderCreatedEvent> stockControl() {
    // event == OrderCreatedEvent
    return event -> {
      log.info("[Event Received] Order: {}, Product: {}, Quantity: {}", 
          event.orderId(), event.productId(), event.quantity());

      // 재고 처리 로직으로 가정
      // 가상 스레드 스케줄러가 블로킹 구간을 논블로킹으로 자동 마운트/언마운트
      try {
        Thread.sleep(150);
        log.info("[Stock Successfully Deducted] Product: {}", event.productId());
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new IllegalStateException("재고 처리 중 스레드 인터럽트 발생", e);
      }
    };
  }
}