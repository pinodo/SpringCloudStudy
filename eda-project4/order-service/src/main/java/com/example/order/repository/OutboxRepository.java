package com.example.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.order.entity.Outbox;
import com.example.order.entity.OutboxStatus;

public interface OutboxRepository extends JpaRepository<Outbox, String> {
    
  // 미처리(PENDING) 상태의 아웃박스 목록 조회
  List<Outbox> findByStatus(OutboxStatus status);
}