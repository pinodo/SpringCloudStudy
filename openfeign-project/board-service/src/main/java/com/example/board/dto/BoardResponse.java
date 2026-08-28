package com.example.board.dto;

public record BoardResponse(
  Long boardId, 
  String title, 
  String content, 
  String writerName  // user-sevice로부터 받아서 채우는 데이터
) { }