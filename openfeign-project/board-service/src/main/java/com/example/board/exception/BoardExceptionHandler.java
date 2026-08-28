package com.example.board.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.common.dto.ErrorResponse;
import com.example.common.exception.GlobalExceptionHandler;

@RestControllerAdvice(basePackages = "com.example.board")
public class BoardExceptionHandler extends GlobalExceptionHandler {

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ErrorResponse.of(e.getMessage(), e.getErrorCode()));
  }
}