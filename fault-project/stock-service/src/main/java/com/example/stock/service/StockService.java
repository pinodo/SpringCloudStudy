package com.example.stock.service;

import org.springframework.stereotype.Service;

import com.example.stock.client.MarketClient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockService {

  private final MarketClient marketClient;

  @Retry(name = "marketService", fallbackMethod = "getFallbackPrice")
  @CircuitBreaker(name = "marketService")
  public String getStockPrice(String ticker) {
    log.info("[StockService] Market Service 호출, ticker: {}", ticker);
    return marketClient.getPrice(ticker);
  }

  public String getFallbackPrice(String ticker, Throwable t) {
    log.error("[StockService] Fallback 호출, ticker: {}", ticker);
    log.error("예외 메시지: {}", t.getMessage());
    return "요청 실패로 직전 Price 안내: 90";
  }
}