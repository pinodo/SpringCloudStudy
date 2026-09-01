package com.example.gateway.filter;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * 인증 검증 필터 (Verification Filter) - Spring Boot 4.1 Gateway MVC 전용
 * 
 * Reactive Mono/Flux 대신 표준 MVC HandlerFilterFunction을 구현합니다.
 * Component 이름을 "VerificationFilter"로 지정해 두고, GatewayRouteConfig.java에서 필터 체이닝합니다.
 */
@Slf4j
@Component("VerificationFilter")
public class VerificationFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

  @Override
  public ServerResponse filter(ServerRequest request, HandlerFunction<ServerResponse> next) throws Exception {
    // 1. 요청 헤더(Header)에서 인증 키(X-Secret-Key) 추출
    String secretKey = request.headers().firstHeader("X-Secret-Key");

    // 2. 인증 키 검증 (null이거나 허가된 키와 일치하지 않는 경우)
    if (secretKey == null || !secretKey.equals("secret1234")) {
      log.warn("[Gateway Alert] Unauthorized access attempt blocked. Key: {}", secretKey);
      
      // ServerResponse를 직접 빌드하여 401 Unauthorized 반환 (백엔드 포워딩 차단)
      return ServerResponse.status(HttpStatus.UNAUTHORIZED)
          .body("Unauthorized: Invalid or missing X-Secret-Key");
    }

    // 3. 인증 성공 시 다음 필터 또는 백엔드 서비스로 요청 진행
    log.info("[Gateway Pass] Verification successful.");
    return next.handle(request);
  }
}