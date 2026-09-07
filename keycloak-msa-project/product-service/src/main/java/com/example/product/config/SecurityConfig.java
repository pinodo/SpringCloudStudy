package com.example.product.config;

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
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      // CSRF 비활성화
      .csrf(AbstractHttpConfigurer::disable)
      
      // 모든 요청은 인증 필요
      .authorizeHttpRequests(auth -> auth
        .anyRequest().authenticated()
      )
      
      // 시큐리티가 JWT 토큰을 가로챈 뒤 검증
      .oauth2ResourceServer(oauth2 -> oauth2
        .jwt(Customizer.withDefaults())
      );
    
    return http.build();
  }
}