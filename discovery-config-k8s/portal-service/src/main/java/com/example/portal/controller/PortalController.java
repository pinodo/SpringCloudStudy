package com.example.portal.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PortalController {

  private final RestTemplate restTemplate = new RestTemplate();

  @GetMapping("/portal")
  public String viewPortal() {
    // 1. IP 주소를 적지 않았습니다.
    // 2. Eureka Client @LoadBalanced도 없습니다.
    // 3. 오직 K8s Service 이름("http://message-service")만 사용합니다.
    // K8s CoreDNS가 이 이름을 내부 IP로 변환(Resolving) 해줍니다.
    String url = "http://message-service:8080/message";

    try {
      String result = restTemplate.getForObject(url, String.class);
      return "<h1>Portal Service</h1>" +
          "<p>Message Service로부터 받은 응답: <strong>" + result + "</strong></p>";
    } catch (Exception e) {
      return "통신 실패: " + e.getMessage();
    }
  }
}