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
}