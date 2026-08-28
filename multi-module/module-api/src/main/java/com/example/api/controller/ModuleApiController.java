package com.example.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.common.MyStringUtil;

@RestController
public class ModuleApiController {

  @GetMapping
  public String hello() {
    return MyStringUtil.addGreeting("John Doe");
  }
}
