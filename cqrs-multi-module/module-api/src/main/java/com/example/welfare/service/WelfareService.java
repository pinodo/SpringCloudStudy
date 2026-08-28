package com.example.welfare.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.member.entity.Member;
import com.example.member.repository.MemberRepository;
import com.example.point.dto.PointHistoryDto;
import com.example.point.mapper.PointHistoryMapper;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WelfareService {

  private final MemberRepository memberRepository;
  private final PointHistoryMapper pointHistoryMapper;

  public Member getMemberById(Long memberId) {
    return memberRepository.findById(memberId)
        .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));
  }

  public List<PointHistoryDto> getPointHistory(Long memberId) {
    return pointHistoryMapper.findPointHistoryByMemberId(memberId);
  }
}
