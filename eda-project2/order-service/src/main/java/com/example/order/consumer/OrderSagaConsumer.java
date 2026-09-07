package com.example.order.consumer;

import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.common.dto.event.StockFailedEvent;
import com.example.common.dto.event.StockReservedEvent;
import com.example.order.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class OrderSagaConsumer {

  private final OrderService orderService;

  @Bean
  public Consumer<StockReservedEvent> stockSuccess() {
    return event -> {
      log.info("[Saga Step] Received StockReservedEvent for Order ID: {}", event.orderId());
      orderService.completeOrder(event.orderId());
    };
  }

  @Bean
  public Consumer<StockFailedEvent> stockFailure() {
    return event -> {
      log.error("[Saga Step] Received StockFailedEvent for Order ID: {}. Reason: {}", 
          event.orderId(), event.reason());
      orderService.cancelOrder(event.orderId());
    };
  }
}