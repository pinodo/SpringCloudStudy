package com.example.board.exception;

import com.example.common.exception.BusinessException;

public class UserNotFoundException extends BusinessException {
  public UserNotFoundException(String message) {
    super(message, "USER_NOT_FOUND"); 
  }
}