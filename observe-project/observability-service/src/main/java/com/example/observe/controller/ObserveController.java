package com.example.observe.controller;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class ObserveController {

  /**
   * 1. 생성되는 메트릭 이름: api_hello_latency_seconds_*
   *    - api_hello_latency_seconds_count (누적 요청 수)
   *    - api_hello_latency_seconds_sum   (누적 소요 시간 합)
   *    - api_hello_latency_seconds_max   (최대 지연 시간)
   * 2. 분산 추적(Zipkin): 메소드 진입 시 Root Span 생성 및 traceId/spanId 부여
   * 3. 분산 로깅(Loki): MDC를 통해 로그에 traceId 자동 바인딩
   */
  @Timed(
      value = "api.hello.latency", 
      description = "Hello API 응답 지연 시간 및 호출 횟수"
  )
  @GetMapping("/hello")
  public String hello(@RequestParam(defaultValue = "World") String name) throws InterruptedException {
    log.info("Incoming request received for name: {}", name);

    // 1. 랜덤 Latency 발생 (0~1000ms: Tracing 구간 타임라인 확인용)
    int latency = ThreadLocalRandom.current().nextInt(1000);
    Thread.sleep(latency);

    // 2. 임계치(800ms) 초과 시 의도적 500 에러 발생 (Loki ERROR 레벨 및 Zipkin Error Tag 검증용)
    if (latency > 800) {
      log.error("High latency threshold exceeded! Latency: {}ms", latency);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Simulated Latency Server Error (" + latency + "ms)");
    }

    log.info("Request processed successfully in {}ms", latency);
    return "Hello, " + name + "! (Latency: " + latency + "ms)";
  }
}