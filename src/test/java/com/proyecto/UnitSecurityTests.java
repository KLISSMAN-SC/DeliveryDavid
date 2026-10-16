package com.proyecto;

import static org.junit.jupiter.api.Assertions.*;

import com.proyecto.security.*;
import com.proyecto.services.FileStorageService;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

class UnitSecurityTests {
  @TempDir Path dir;

  @Test
  void htmlDisguisedAsImageIsRejected() {
    var storage = new FileStorageService(dir.toString(), 100000000);
    assertThrows(
        BusinessException.class,
        () ->
            storage.storeImage(
                new MockMultipartFile(
                    "archivoImagen",
                    "foto.png",
                    "image/png",
                    "<html><script>alert(1)</script></html>".getBytes()),
                "productos"));
  }

  @Test
  void realImageIsReencodedAndRollbackDeletesIt() throws Exception {
    var bytes = new ByteArrayOutputStream();
    ImageIO.write(new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB), "png", bytes);
    var storage = new FileStorageService(dir.toString(), 100000000);
    Path file;
    try (var image =
        storage.storeImage(
            new MockMultipartFile(
                "archivoImagen", "../../foto.html", "text/html", bytes.toByteArray()),
            "productos")) {
      assertTrue(image.filename().matches("[a-f0-9-]{36}\\.png"));
      file = dir.resolve("productos").resolve(image.filename());
      assertNotNull(ImageIO.read(file.toFile()));
    }
    assertFalse(Files.exists(file));
  }

  @Test
  void secureTokensHaveExpectedFormatAndHash() {
    var a = TokenTools.token();
    var b = TokenTools.token();
    assertTrue(a.matches("[A-Za-z0-9_-]{43}"));
    assertNotEquals(a, b);
    assertTrue(TokenTools.matches(a, TokenTools.hash(a)));
    assertFalse(TokenTools.matches(b, TokenTools.hash(a)));
  }

  @Test
  void passwordPolicyRejectsWeakAndOverlong() {
    var p = new PasswordPolicy();
    assertThrows(BusinessException.class, () -> p.validate("x"));
    assertThrows(BusinessException.class, () -> p.validate("A".repeat(80) + "a!"));
    assertDoesNotThrow(() -> p.validate("FrasePersonalSegura!2026"));
  }

  @Test
  void rateLimitFailsClosed() {
    var limits = new RateLimits(new SecurityAudit());
    limits.check("unit", "test", 1, java.time.Duration.ofMinutes(1));
    assertEquals(
        org.springframework.http.HttpStatus.TOO_MANY_REQUESTS,
        assertThrows(
                BusinessException.class,
                () -> limits.check("unit", "test", 1, java.time.Duration.ofMinutes(1)))
            .getStatus());
  }
}
