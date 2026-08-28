package com.example.board.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Logger;
import feign.codec.ErrorDecoder;

@Configuration
public class OpenFeignConfig {

  @Bean
  public Logger.Level feignLoggerLevel() {
    return Logger.Level.FULL; // 헤더, 바디, 상태코드를 콘솔에 모두 출력하도록 선언
  }

  @Bean
  public ErrorDecoder errorDecoder() {
    return new OpenFeignErrorDecoder(); // 작성한 커스텀 에러 디코더 바인딩 빈 등록
  }
}