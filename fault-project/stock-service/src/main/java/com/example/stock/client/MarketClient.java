package com.example.stock.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "market-service", url = "http://localhost:8081")
public interface MarketClient {
  @GetMapping("/api/markets/price/{ticker}")
  String getPrice(@PathVariable("ticker") String ticker);
}