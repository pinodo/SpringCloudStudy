package com.example.order.entity;

public enum OutboxStatus {
  PENDING,    // 메시지 발행 대기 중
  PROCESSED   // 메시지 브로커 전송 완료
}