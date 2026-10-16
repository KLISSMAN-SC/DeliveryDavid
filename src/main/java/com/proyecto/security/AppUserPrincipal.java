package com.proyecto.security;

import com.proyecto.model.Usuario;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class AppUserPrincipal extends User {
  private final Integer usuarioId;
  private final long securityVersion;

  public AppUserPrincipal(Usuario u, String role) {
    super(
        u.getCorreoElectronico(),
        u.getPassword(),
        List.of(new SimpleGrantedAuthority("ROLE_" + role)));
    usuarioId = u.getIdUsuario();
    securityVersion = u.getSecurityVersion() == null ? 0 : u.getSecurityVersion();
  }

  public Integer getUsuarioId() {
    return usuarioId;
  }

  public long getSecurityVersion() {
    return securityVersion;
  }
}
