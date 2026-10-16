package com.proyecto.security;

import java.math.BigDecimal;
import java.net.URI;

public final class InputChecks {
  private InputChecks() {}

  public static String text(String value, int max) {
    if (value == null || value.isBlank() || value.length() > max)
      throw BusinessException.invalid("Revisa los campos obligatorios y su longitud.");
    return value.strip();
  }

  public static void optional(String value, int max) {
    if (value != null && value.length() > max)
      throw BusinessException.invalid("El texto es demasiado largo.");
  }

  public static String phone(String value) {
    if (value == null || !value.matches("[0-9]{9,15}"))
      throw BusinessException.invalid("El teléfono debe tener entre 9 y 15 dígitos.");
    return value;
  }

  public static void money(BigDecimal value) {
    if (value == null
        || value.signum() < 0
        || value.compareTo(new BigDecimal("99999999.99")) > 0
        || value.stripTrailingZeros().scale() > 2)
      throw BusinessException.invalid(
          "Importe no válido; utiliza hasta dos decimales y un valor no negativo.");
  }

  public static void coordinates(BigDecimal lat, BigDecimal lng) {
    if (lat == null
        || lng == null
        || lat.abs().compareTo(new BigDecimal("90")) > 0
        || lng.abs().compareTo(new BigDecimal("180")) > 0)
      throw BusinessException.invalid("Las coordenadas no son válidas.");
  }

  public static void httpsUrl(String value) {
    if (value == null || value.isBlank()) return;
    try {
      URI u = URI.create(value);
      if (value.length() > 255
          || !"https".equalsIgnoreCase(u.getScheme())
          || u.getHost() == null
          || u.getUserInfo() != null) throw new IllegalArgumentException();
    } catch (IllegalArgumentException e) {
      throw BusinessException.invalid("Los enlaces deben ser direcciones HTTPS válidas.");
    }
  }
}
