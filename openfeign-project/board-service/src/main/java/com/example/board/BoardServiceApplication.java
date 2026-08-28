package com.example.board;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients  // [필수] @FeignClient 인터페이스 자동 검색
public class BoardServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(BoardServiceApplication.class, args);
  }
}