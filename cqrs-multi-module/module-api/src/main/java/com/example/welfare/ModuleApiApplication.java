package com.example.welfare;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

// 1. 일반 컴포넌트 스캔 및 2~4번 기술별 스캔 영역을 전역 상위 패키지(com.example)로 일제히 확장함
@SpringBootApplication(scanBasePackages = "com.example")
@org.springframework.boot.persistence.autoconfigure.EntityScan(basePackages = "com.example")
@EnableJpaRepositories(basePackages = "com.example")
@MapperScan(basePackages = "com.example", annotationClass = Mapper.class)
public class ModuleApiApplication {
  public static void main(String[] args) {
    SpringApplication.run(ModuleApiApplication.class, args);
  }
}