package com.example.stock.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.stock.entity.Stock;

import jakarta.persistence.LockModeType;

public interface StockRepository extends JpaRepository<Stock, String> {

  // 동시성 보호를 위한 비관적 쓰기 락 추가
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT s FROM Stock s WHERE s.productId = :productId")
  Optional<Stock> findByIdWithLock(@Param("productId") String productId);
}