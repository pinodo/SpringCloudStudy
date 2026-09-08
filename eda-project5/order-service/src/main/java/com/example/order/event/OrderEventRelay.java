package com.example.order.event;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.common.dto.event.OrderCreatedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventRelay {

  // 비즈니스 로직 내부에서 필요 시점에 동적으로 이벤트를 밀어내기 위한 프레임워크 도구
  private final StreamBridge streamBridge;

  // publishEvent()가 실행되는 즉시 handleOrderCommitted() 메서드가 호출되는 것이 아니라,
  // DB 트랜잭션이 물리적으로 Commit 완료되는 순간 스프링에 의해서 동작함
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleOrderCommitted(OrderCreatedEvent event) {
    // 지정된 출력 채널 "orderCreated-out-0"으로 이벤트 데이터 발송
    // Jackson 라이브러리에 의해 JSON 문자열 형태로 마샬링되어 RabbitMQ로 인젝션됨
    streamBridge.send("orderCreated-out-0", event);
    log.info("[Event Published to RabbitMQ] Payload: {}", event);
  }
}