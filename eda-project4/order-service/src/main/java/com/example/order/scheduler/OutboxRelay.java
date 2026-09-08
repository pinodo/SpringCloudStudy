package com.example.order.scheduler;

import java.util.List;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.order.entity.Outbox;
import com.example.order.entity.OutboxStatus;
import com.example.order.repository.OutboxRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxRelay {

  private final OutboxRepository outboxRepository;
  private final StreamBridge streamBridge;

  @Scheduled(fixedDelay = 500)  // 0.5초 주기로 Polling
  @Transactional
  public void publishPendingEvents() {
    List<Outbox> pendingEvents = outboxRepository.findByStatus(OutboxStatus.PENDING);

    if (pendingEvents.isEmpty()) {
      return;
    }

    log.info("[Outbox Relay] Found {} pending event(s).", pendingEvents.size());

    for (Outbox outbox : pendingEvents) {
      try {
        // outbox.getPayload()가 이미 JSON 문자열이므로 그대로 발행
        boolean sent = streamBridge.send(outbox.getDestination(), outbox.getPayload());
        
        if (sent) {
          outbox.complete(); // Dirty Checking으로 PROCESSED 반영
          log.info("[Outbox Relay Success] Published Outbox ID: {}", outbox.getId());
        } else {
          log.warn("[Outbox Relay Warning] StreamBridge.send returned false. ID: {}", outbox.getId());
        }
      } catch (Exception e) {
        log.error("[Outbox Relay Failed] Message Broker is unreachable. Will retry in next cycle. ID: {}", outbox.getId());
        break; // 브로커 장애 시 연속 에러 로그 방지를 위해 현재 주기 중단
      }
    }
  }
}