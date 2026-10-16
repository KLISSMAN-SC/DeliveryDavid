package com.proyecto;

import com.proyecto.services.EmailService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.*;

/**
 * Servidor de pruebas únicamente: H2 en memoria, loopback y correo simulado. No se incluye en el
 * JAR de producción.
 */
public class FrontendSmokeServer {
  public static void main(String[] args) {
    System.setProperty("spring.profiles.active", "test");
    System.setProperty("server.port", "8085");
    System.setProperty("google.maps.api-key", "smoke-map-key");
    System.setProperty("server.address", "127.0.0.1");
    var app = new SpringApplication(ProyectoDavidApplication.class, Smoke.class);
    var context = app.run(args);
    TestData.reset(context);
    System.out.println("SMOKE_READY");
  }

  @TestConfiguration
  static class Smoke {
    @Bean
    @Primary
    EmailService smokeMailer() {
      return new EmailService() {
        @Override
        public void enviarCorreoVerificacion(String a, String b, String c) {}

        @Override
        public void enviarCorreoRecuperacion(String a, String b, String c) {}
      };
    }
  }
}
