package com.example.point.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.point.dto.PointHistoryDto;

@Mapper
public interface PointHistoryMapper {
  List<PointHistoryDto> findPointHistoryByMemberId(Long memberId);
}
