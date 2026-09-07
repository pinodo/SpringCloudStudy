package com.example.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
  
  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      // CSRF 비활성화
      .csrf(AbstractHttpConfigurer::disable)
      
      // "/api/admin/**" 경로 요청은 반드시 "admin" 권한 필요
      // .hasRole("admin")는 내부적으로 "ROLE_admin"가 있는지 조회
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/api/admin/**").hasRole("admin")
        .anyRequest().authenticated()
      )
      
      // 기본 JWT 변환기 대신 커스텀한 JwtAuthenticationConverter 사용
      .oauth2ResourceServer(oauth2 -> oauth2
        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
      );
    
    return http.build();
  }

  // JWT 변환기 커스텀
  // 토큰이 들어오면, KeycloakRoleConverter를 사용해서 "roles" 추출한 뒤 "ROLE_"을 앞에 붙여줌
  private JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
    converter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
    return converter;
  }
}