package com.example.stock.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.stock.entity.Stock;
import com.example.stock.repository.StockRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {

  private final StockRepository stockRepository;

  @Transactional
  public void decreaseStock(String productId, Integer quantity) {
    Stock stock = stockRepository.findByIdWithLock(productId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품 ID: " + productId));

    stock.decrease(quantity);
    log.info("[Stock Deducted] Product: {}, Deducted: {}, Remaining: {}", 
        productId, quantity, stock.getQuantity());
  }

  // [V2 신규] 보상 트랜잭션: 차감되었던 재고를 비관적 락으로 원상복구
  @Transactional
  public void restoreStock(String productId, Integer quantity) {
    Stock stock = stockRepository.findByIdWithLock(productId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품 ID: " + productId));

    stock.increase(quantity);
    log.warn("[Stock Restored - Compensation] Product: {}, Restored: +{}, Total Quantity: {}", 
        productId, quantity, stock.getQuantity());
  }
}