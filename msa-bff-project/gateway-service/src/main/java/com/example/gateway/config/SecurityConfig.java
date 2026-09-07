package com.example.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      // 세션 생성 정책: 필요 시 항상 세션 생성 (ALWAYS 또는 IF_REQUIRED)
      .sessionManagement(session -> session
        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
      )
      .csrf(csrf -> csrf
        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        // 외부 클라이언트가 직접 호출하는 회원가입 엔드포인트는 CSRF 검증 제외
        .ignoringRequestMatchers("/api/users/signup", "/api/users/signup/**")
      )
      .authorizeHttpRequests(auth -> auth
        .requestMatchers(
            "/login/**", 
            "/oauth2/**", 
            "/public/**", 
            "/api/users/signup", 
            "/api/users/signup/**")
        .permitAll()
        .anyRequest().authenticated()
      )
      .oauth2Login(Customizer.withDefaults())
      .logout(logout -> logout
        .logoutUrl("/logout")
        .logoutSuccessUrl("/")
      );

    return http.build();
  }
}