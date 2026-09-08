package com.example.stock.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stocks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Stock {

  @Id
  private String productId;
  private Integer quantity;

  public void decrease(Integer amount) {
    if (this.quantity < amount) {
      throw new IllegalStateException(String.format("재고 부족 (요청: %d, 잔여: %d)", amount, this.quantity));
    }
    this.quantity -= amount;
  }

  public void increase(Integer amount) {
    this.quantity += amount;
  }
}