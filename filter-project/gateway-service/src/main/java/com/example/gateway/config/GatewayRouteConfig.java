package com.example.gateway.config;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

import java.net.URI;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import com.example.gateway.filter.VerificationFilter;

@Configuration
public class GatewayRouteConfig {

  @Bean
  public RouterFunction<ServerResponse> paymentRoute(VerificationFilter verificationFilter) {
    return route("payment-service-route")
        // 1. 요청 매칭 (Predicate: Path 조건 지정)
        .GET("/api/payments/**", http())
        
        // 2. before(uri(...))로 포워딩 대상 lb:// 스키마 지정
        .before(uri(URI.create("lb://PAYMENT-SERVICE")))
        
        // 3. 커스텀 검증 필터 실행
        .filter(verificationFilter)
        
        // 4. lb() 필터를 통해 lb:// -> 실제 http://IP:Port 변환 처리
        .filter(lb("PAYMENT-SERVICE"))

        .build();
  }
}