package com.proyecto.services;

import com.proyecto.repository.UsuarioRepository;
import com.proyecto.security.AppUserPrincipal;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
  private final UsuarioRepository users;
  private final int lockMinutes;

  public CustomUserDetailsService(
      UsuarioRepository users, @Value("${app.security.login-lock-minutes:15}") int lockMinutes) {
    this.users = users;
    this.lockMinutes = lockMinutes;
  }

  @Override
  public UserDetails loadUserByUsername(String name) {
    var u =
        users
            .findByCorreoElectronico(UserAccountService.normalizeEmail(name))
            .orElseThrow(() -> new UsernameNotFoundException("Credenciales no válidas"));
    if (u.getFechaBloqueo() != null
        && u.getFechaBloqueo()
            .plusMinutes(lockMinutes)
            .isAfter(LocalDateTime.now(java.time.ZoneOffset.UTC)))
      throw new LockedException("Credenciales no válidas");
    if (u.getRol() == null) throw new UsernameNotFoundException("Credenciales no válidas");
    String role =
        switch (u.getRol().getIdRol()) {
          case 1 -> "ADMINISTRADOR";
          case 2 -> "CLIENTE";
          case 3 -> "REPARTIDOR";
          default -> throw new UsernameNotFoundException("Credenciales no válidas");
        };
    return new AppUserPrincipal(u, role);
  }
}
