package com.example.gw.filter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order(-1) // 필터 실행 우선순위 지정
public class GlobalLoggingFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

  @Override
  public ServerResponse filter(ServerRequest request, HandlerFunction<ServerResponse> next) throws Exception {
    // Pre Filter: 요청 정보 기록
    log.info("Global Filter - Request Path: {}", request.path());
    log.info("Global Filter - Request URI: {}", request.uri());

    // 다음 필터/라우트로 요청 전달 (동기 블로킹 방식, Virtual Threads가 처리)
    ServerResponse response = next.handle(request);

    // Post Filter: 응답 Status 기록
    log.info("Global Filter - Response Status: {}", response.statusCode().value());

    return response;
  }
}