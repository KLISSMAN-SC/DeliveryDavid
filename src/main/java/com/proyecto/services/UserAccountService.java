package com.proyecto.services;

import com.proyecto.dto.*;
import com.proyecto.model.Usuario;
import com.proyecto.repository.*;
import com.proyecto.security.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAccountService {
  private final UsuarioRepository users;
  private final RolRepository roles;
  private final PasswordEncoder encoder;
  private final PasswordPolicy policy;
  private final EmailService email;
  private final SecurityAudit audit;

  public UserAccountService(
      UsuarioRepository users,
      RolRepository roles,
      PasswordEncoder encoder,
      PasswordPolicy policy,
      EmailService email,
      SecurityAudit audit) {
    this.users = users;
    this.roles = roles;
    this.encoder = encoder;
    this.policy = policy;
    this.email = email;
    this.audit = audit;
  }

  public static String normalizeEmail(String input) {
    return input == null ? "" : input.strip().toLowerCase(Locale.ROOT);
  }

  @Transactional
  public Usuario createClient(
      String nombres, String apellidos, String telefono, String correo, String hash) {
    if (users.findByCorreoElectronico(correo).isPresent()
        || users.findByTelefono(telefono).isPresent())
      throw BusinessException.invalid("No se pudo completar el registro con esos datos.");
    Usuario u = new Usuario();
    u.setNombres(nombres.strip());
    u.setApellidos(apellidos.strip());
    u.setTelefono(telefono);
    u.setCorreoElectronico(correo);
    u.setPassword(hash);
    u.setRol(
        roles
            .findById(2)
            .orElseThrow(() -> new IllegalStateException("El rol CLIENTE no está configurado")));
    u.setSecurityVersion(0L);
    u.setIntentosFallidos(0);
    users.saveAndFlush(u);
    audit.event("ACCOUNT_CREATED", u.getIdUsuario());
    return u;
  }

  @Async("mailExecutor")
  @Transactional
  public void requestReset(String rawEmail) {
    String correo = normalizeEmail(rawEmail);
    var optional = users.findForUpdateByEmail(correo);
    if (optional.isEmpty()) return;
    var u = optional.get();
    String token = TokenTools.token();
    u.setTokenPassword(TokenTools.hash(token));
    u.setTokenPasswordExpira(Instant.now().plus(Duration.ofMinutes(15)));
    users.saveAndFlush(u);
    try {
      email.enviarCorreoRecuperacion(correo, token, u.getNombres() + " " + u.getApellidos());
      audit.event("RECOVERY_SENT", u.getIdUsuario());
    } catch (RuntimeException e) {
      u.setTokenPassword(null);
      u.setTokenPasswordExpira(null);
      users.saveAndFlush(u);
      audit.alert("RECOVERY_DELIVERY_FAILED", u.getIdUsuario());
    }
  }

  public boolean validReset(String token) {
    if (token == null || !token.matches("[A-Za-z0-9_-]{43}")) return false;
    return users
        .findByTokenPassword(TokenTools.hash(token))
        .filter(
            u ->
                u.getTokenPasswordExpira() != null
                    && u.getTokenPasswordExpira().isAfter(Instant.now()))
        .isPresent();
  }

  @Transactional
  public void reset(String token, String password) {
    policy.validate(password);
    if (token == null || !token.matches("[A-Za-z0-9_-]{43}"))
      throw BusinessException.invalid("El enlace no es válido o ha vencido.");
    Usuario u =
        users
            .findForReset(TokenTools.hash(token))
            .filter(
                x ->
                    x.getTokenPasswordExpira() != null
                        && x.getTokenPasswordExpira().isAfter(Instant.now()))
            .orElseThrow(() -> BusinessException.invalid("El enlace no es válido o ha vencido."));
    u.setPassword(encoder.encode(password));
    revoke(u);
    u.setFechaBloqueo(null);
    u.setIntentosFallidos(0);
    users.saveAndFlush(u);
    audit.event("PASSWORD_RESET", u.getIdUsuario());
  }

  @Transactional
  public boolean updateProfile(String correo, PerfilRequest form) {
    Usuario u = users.findForUpdateByEmail(correo).orElseThrow(BusinessException::missing);
    if (users
        .findByTelefono(form.telefono())
        .filter(x -> !x.getIdUsuario().equals(u.getIdUsuario()))
        .isPresent()) throw BusinessException.invalid("El teléfono ya está registrado.");
    boolean passwordChange = form.password() != null && !form.password().isBlank();
    if (passwordChange) {
      if (form.passwordActual() == null || !encoder.matches(form.passwordActual(), u.getPassword()))
        throw BusinessException.invalid("La contraseña actual no es correcta.");
      policy.validate(form.password());
      u.setPassword(encoder.encode(form.password()));
      revoke(u);
    }
    u.setNombres(form.nombres().strip());
    u.setApellidos(form.apellidos().strip());
    u.setTelefono(form.telefono());
    users.saveAndFlush(u);
    audit.event("PROFILE_CHANGED", u.getIdUsuario());
    return passwordChange;
  }

  public static void revoke(Usuario u) {
    u.setSecurityVersion((u.getSecurityVersion() == null ? 0 : u.getSecurityVersion()) + 1);
    u.setTokenPassword(null);
    u.setTokenPasswordExpira(null);
  }
}
