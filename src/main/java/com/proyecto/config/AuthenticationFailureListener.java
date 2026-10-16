package com.proyecto.config;

import com.proyecto.repository.UsuarioRepository;
import com.proyecto.security.SecurityAudit;
import com.proyecto.services.UserAccountService;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AuthenticationFailureListener {
  private final UsuarioRepository users;
  private final SecurityAudit audit;
  private final int lockMinutes;

  public AuthenticationFailureListener(
      UsuarioRepository users,
      SecurityAudit audit,
      @org.springframework.beans.factory.annotation.Value("${app.security.login-lock-minutes:15}")
          int lockMinutes) {
    this.users = users;
    this.audit = audit;
    this.lockMinutes = lockMinutes;
  }

  @EventListener
  @Transactional
  public void failure(AuthenticationFailureBadCredentialsEvent event) {
    users
        .findForUpdateByEmail(
            UserAccountService.normalizeEmail(event.getAuthentication().getName()))
        .ifPresent(
            u -> {
              int count = u.getIntentosFallidos() == null ? 0 : u.getIntentosFallidos();
              // Un bloqueo vencido empieza una ventana nueva de intentos.
              if (u.getFechaBloqueo() != null
                  && u.getFechaBloqueo()
                      .plusMinutes(lockMinutes)
                      .isBefore(LocalDateTime.now(ZoneOffset.UTC))) count = 0;
              u.setIntentosFallidos(Math.min(count + 1, 5));
              if (u.getIntentosFallidos() >= 5) {
                u.setFechaBloqueo(LocalDateTime.now(ZoneOffset.UTC));
                audit.alert("ACCOUNT_TEMPORARILY_LOCKED", u.getIdUsuario());
              }
              users.save(u);
            });
  }
}
