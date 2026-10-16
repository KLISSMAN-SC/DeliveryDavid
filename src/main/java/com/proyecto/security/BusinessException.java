package com.proyecto.security;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {
  private final HttpStatus status;

  public BusinessException(HttpStatus status, String message) {
    super(message);
    this.status = status;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public static BusinessException invalid(String message) {
    return new BusinessException(HttpStatus.BAD_REQUEST, message);
  }

  public static BusinessException missing() {
    return new BusinessException(HttpStatus.NOT_FOUND, "Recurso no encontrado.");
  }
}
