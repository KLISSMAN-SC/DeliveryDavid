package com.proyecto.security;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class PasswordPolicy {
  private static final Set<String> BLOCKED =
      Set.of(
          "password123!",
          "password1234!",
          "contraseña123!",
          "contrasena123!",
          "qwerty123456!",
          "administrator1!",
          "administrador1!",
          "delivery12345!",
          "123456789012");

  public void validate(String value) {
    if (value == null
        || value.length() < 12
        || value.getBytes(StandardCharsets.UTF_8).length > 72
        || !value.matches("(?s).*[A-Z].*")
        || !value.matches("(?s).*[a-z].*")
        || !value.matches("(?s).*[^a-zA-Z0-9].*")
        || BLOCKED.contains(value.toLowerCase(Locale.ROOT)))
      throw BusinessException.invalid(
          "La contraseña debe tener al menos 12 caracteres, mayúscula, minúscula y un carácter"
              + " especial; máximo 72 bytes UTF-8. Evita contraseñas comunes.");
  }
}
