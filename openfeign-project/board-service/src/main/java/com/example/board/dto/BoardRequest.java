package com.example.board.dto;

public record BoardRequest(
  String title, 
  String content, 
  Long userId
) { }