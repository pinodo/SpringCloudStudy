package com.example.welfare.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.member.entity.Member;
import com.example.welfare.service.WelfareService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class WelfareController {

  private final WelfareService welfareService;

  @GetMapping("/{memberId}")
  public ResponseEntity<String> getBalance(@PathVariable(value = "memberId") Long memberId) {
    try {
      Member member = welfareService.getMemberById(memberId);
      return ResponseEntity.ok("잔액: " + member.getBalance());
    } catch (Exception e) {
      // return ResponseEntity.notFound().build();
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }

  @GetMapping("/{memberId}/history")
  public ResponseEntity<?> getPointHistories(@PathVariable(value = "memberId") Long memberId) {
    return ResponseEntity.ok(welfareService.getPointHistory(memberId));
  }
}
