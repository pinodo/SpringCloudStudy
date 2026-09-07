package com.example.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  SecurityFilterChain securityWebFilterChain(HttpSecurity http) {
    http
      // 1. CSRF 비활성화
      .csrf(AbstractHttpConfigurer::disable)

      // 2. 요청별 권한 설정 (Authorization)
      .authorizeHttpRequests(exchanges -> exchanges
        // 로그인 요청 페이지 및 공개 경로 허용
        .requestMatchers("/login/**", "/oauth2**").permitAll()
        .anyRequest().authenticated()
      )
      
      // 3. OAuth2 Login 활성화
      // 로그인 폼 redirect 및 authorization_code 처리
      .oauth2Login(Customizer.withDefaults()
    );

    return http.build();
  }
}