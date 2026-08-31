package com.example.market.controller;

import java.util.Random;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/markets")
public class MarketController {

  private final Random random = new Random();

  @GetMapping("/price/{ticker}")
  public ResponseEntity<String> getPrice(@PathVariable String ticker) {
    int chance = random.nextInt(10);  // 0 ~ 9

    if (chance < 3) {  // 30% 장애 발생
      log.info("[Market Service] Error Occurd for {}", ticker);
      return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("Market Service Unavailable");
    }

    log.info("[Market Service] Query Succeed for {}", ticker);
    return ResponseEntity.ok("조회된 가격: 100.00");
  }
}