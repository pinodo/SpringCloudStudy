package com.example.order.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Order {

  @Id
  private String orderId;

  private String productId;
  private Integer quantity;

  @Enumerated(EnumType.STRING)
  private OrderStatus status;

  public void complete() {
    if (this.status == OrderStatus.PENDING) {
      this.status = OrderStatus.COMPLETED;
    }
  }

  public void cancel() {
    if (this.status == OrderStatus.PENDING) {
      this.status = OrderStatus.CANCELLED;
    }
  }
}