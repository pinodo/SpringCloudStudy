package com.example.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

  @Id
  @Column(name = "user_id", length = 36)
  private String userId;  // Keycloak UUID 공유 식별키

  @Column(nullable = false, length = 50)
  private String nickname;

  @Column(name = "phone_number", nullable = false, length = 20)
  private String phoneNumber;

  @Column(nullable = false, length = 255)
  private String address;
}