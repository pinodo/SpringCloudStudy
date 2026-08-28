package com.example.board.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.board.dto.BoardRequest;
import com.example.board.dto.BoardResponse;
import com.example.board.service.BoardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardController {

  private final BoardService boardService;

  @PostMapping
  public ResponseEntity<BoardResponse> createBoard(@RequestBody BoardRequest request) {
    BoardResponse response = boardService.createBoard(request);
    return ResponseEntity.ok(response);
  }
}