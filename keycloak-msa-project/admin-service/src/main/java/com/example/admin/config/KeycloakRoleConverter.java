package com.example.admin.config;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

  @Override
  public Collection<GrantedAuthority> convert(Jwt jwt) {
    // 1. JWT 내부의 'realm_access' 클레임 추출
    // Keycloak은 기본적으로 Realm 레벨의 역할을 'realm_access'라는 키 아래에 JSON 객체 형태로 저장함
    // 예: "realm_access": { "roles": ["admin", "user"] }
    Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");

    // 권한 정보가 없으면 빈 리스트 반환 (NullPointerException 방지)
    if (realmAccess == null || realmAccess.isEmpty()) {
      return List.of();
    }

    // 2. 'roles' 키에서 실제 역할 이름 리스트 추출
    List<String> roles = (List<String>) realmAccess.get("roles");

    // 3. 역할 이름을 GrantedAuthority 객체로 매핑 (접두사 'ROLE_' 추가)
    // Spring Security의 hasRole("admin") 메소드는 기본적으로 "ROLE_" 접두사를 기대하므로,
    // "admin" -> "ROLE_admin"로 변환해주어야 함
    return roles.stream()
      .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
      .collect(Collectors.toList());
  }
}
