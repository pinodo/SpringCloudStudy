package com.example.user.service;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import com.example.user.dto.SignUpRequest;
import com.example.user.dto.UserProfileResponse;
import com.example.user.entity.User;
import com.example.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

  private final UserRepository userRepository;
  private final RestClient restClient;

  @Value("${keycloak.server-url}")
  private String serverUrl;

  @Value("${keycloak.realm}")
  private String realm;

  @Value("${keycloak.client-id}")
  private String clientId;

  @Value("${keycloak.username}")
  private String adminUsername;

  @Value("${keycloak.password}")
  private String adminPassword;

  @Value("${keycloak.target-realm}")
  private String targetRealm;

  @Transactional
  public void signUp(SignUpRequest request) {
    // 1. Admin Token 발급
    String adminAccessToken = getAdminAccessToken();

    // 2. Keycloak 요청 Payload 생성
    Map<String, Object> userRep = Map.of(
        "username", request.username(),
        "email", request.email(),
        "enabled", true,
        "credentials", List.of(Map.of(
            "type", "password",
            "value", request.password(),
            "temporary", false
        ))
    );

    // 3. Keycloak Admin API 호출
    ResponseEntity<Void> response = restClient.post()
        .uri(serverUrl + "/admin/realms/" + targetRealm + "/users")
        .header("Authorization", "Bearer " + adminAccessToken)
        .contentType(MediaType.APPLICATION_JSON)
        .body(userRep)
        .retrieve()
        .toBodilessEntity();

    if (response.getStatusCode().value() != 201) {
      log.error("[Keycloak Error] 유저 생성 실패: {}", response.getStatusCode());
      throw new RuntimeException("Keycloak 사용자 등록에 실패했습니다.");
    }

    // 4. 생성된 UUID 파싱
    URI location = response.getHeaders().getLocation();
    if (location == null) {
      throw new RuntimeException("응답 Location 헤더에서 UUID를 찾을 수 없습니다.");
    }

    String path = location.getPath();
    String keycloakUserId = path.substring(path.lastIndexOf("/") + 1);

    // 5. 로컬 MySQL 영속화
    User localUser = User.builder()
        .userId(keycloakUserId)
        .nickname(request.nickname())
        .phoneNumber(request.phoneNumber())
        .address(request.address())
        .build();

    userRepository.save(localUser);
  }

  public UserProfileResponse getMyProfile(String keycloakUserId, String username, String email) {
    User user = userRepository.findById(keycloakUserId)
        .orElseThrow(() -> new RuntimeException("사용자 프로필 정보를 찾을 수 없습니다."));

    return new UserProfileResponse(
        user.getUserId(),
        username,
        email,
        user.getNickname(),
        user.getPhoneNumber(),
        user.getAddress()
    );
  }

  private String getAdminAccessToken() {
    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add("client_id", clientId);
    formData.add("grant_type", "password");
    formData.add("username", adminUsername);
    formData.add("password", adminPassword);

    Map<?, ?> response = restClient.post()
        .uri(serverUrl + "/realms/" + realm + "/protocol/openid-connect/token")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(formData)
        .retrieve()
        .body(Map.class);

    if (response != null && response.containsKey("access_token")) {
      return (String) response.get("access_token");
    }
    throw new RuntimeException("Keycloak Admin 토큰 획득에 실패했습니다.");
  }
}