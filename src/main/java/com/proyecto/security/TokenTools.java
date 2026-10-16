package com.proyecto.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

public final class TokenTools {
  private static final SecureRandom RANDOM = new SecureRandom();

  private TokenTools() {}

  public static String token() {
    byte[] bytes = new byte[32];
    RANDOM.nextBytes(bytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }

  public static String code() {
    return String.format(java.util.Locale.ROOT, "%06d", RANDOM.nextInt(1000000));
  }

  public static String hash(String value) {
    try {
      return HexFormat.of()
          .formatHex(
              MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
  }

  public static boolean matches(String input, String digest) {
    return input != null
        && digest != null
        && MessageDigest.isEqual(
            hash(input).getBytes(StandardCharsets.US_ASCII),
            digest.getBytes(StandardCharsets.US_ASCII));
  }
}
