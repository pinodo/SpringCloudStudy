package com.example.order.entity;

import java.time.Instant;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "outbox")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Outbox {

  @Id
  private String id;

  @Column(nullable = false, length = 50)
  private String aggregateType;   // "ORDER"

  @Column(nullable = false, length = 255)
  private String aggregateId;     // orderId

  @Column(nullable = false, length = 100)
  private String destination;     // 발행 목적지 Exchange ("order-created-topic")

  @Column(nullable = false, length = 100)
  private String type;            // 이벤트/커맨드 클래스명 ("OrderCreatedEvent")

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "json", nullable = false)
  private String payload;         // JSON 데이터

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private OutboxStatus status;    // PENDING, PROCESSED (Polling에서 추적용으로 사용)

  @Column(nullable = false)
  private Instant createdAt;

  // Polling Relay에서 브로커 전송 완료 시 상태 변경용
  public void complete() {
    this.status = OutboxStatus.PROCESSED;
  }
}