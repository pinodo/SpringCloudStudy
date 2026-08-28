package com.example.point.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class PointHistoryDto {
  private Long id;
  private Long memberId;
  private Long amount;
  private String type;
  private LocalDateTime createdAt;
}
