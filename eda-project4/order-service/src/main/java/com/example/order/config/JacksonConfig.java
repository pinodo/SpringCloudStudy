package com.example.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class JacksonConfig {

  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    // Instant, LocalDateTime 등 Java 8+ 날짜/시간 타입 직렬화 모듈 등록
    objectMapper.registerModule(new JavaTimeModule());
    // 날짜를 숫자(Timestamp)가 아닌 ISO-8601 문자열("2026-09-07T...")로 직렬화
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    return objectMapper;
  }
}