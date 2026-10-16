package com.proyecto.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CsrfController {
  @GetMapping("/csrf")
  public ResponseEntity<Map<String, String>> csrf(CsrfToken token) {
    return ResponseEntity.ok()
        .cacheControl(org.springframework.http.CacheControl.noStore())
        .body(Map.of("token", token.getToken(), "headerName", token.getHeaderName()));
  }
}
