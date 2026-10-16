package com.proyecto.config;

import com.proyecto.repository.UsuarioRepository;
import com.proyecto.security.SecurityAudit;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AuthenticationSuccessListener {
  private final UsuarioRepository users;
  private final SecurityAudit audit;

  public AuthenticationSuccessListener(UsuarioRepository users, SecurityAudit audit) {
    this.users = users;
    this.audit = audit;
  }

  @EventListener
  @Transactional
  public void success(AuthenticationSuccessEvent event) {
    users
        .findForUpdateByEmail(event.getAuthentication().getName())
        .ifPresent(
            u -> {
              u.setIntentosFallidos(0);
              u.setFechaBloqueo(null);
              users.save(u);
              audit.event("LOGIN_SUCCESS", u.getIdUsuario());
            });
  }
}
