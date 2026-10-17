package com.proyecto.controller;

import com.proyecto.dto.RegistroRequest;
import com.proyecto.security.*;
import com.proyecto.services.*;
import jakarta.servlet.http.*;
import jakarta.validation.Valid;
import java.time.Duration;
import java.time.Instant;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistroController {
  
	private record Pending(
      String nombres,
      String apellidos,
      String telefono,
      String correo,
      String passwordHash,
      String codeHash,
      Instant expires,
      int attempts,
      String versionTerminos,
      String versionPrivacidad,
      Instant fechaAceptacionLegal)
      implements java.io.Serializable {}

  private final PasswordEncoder encoder;
  private final PasswordPolicy policy;
  private final EmailService email;
  private final UserAccountService accounts;
  private final CustomUserDetailsService details;
  private final RateLimits limits;

  public RegistroController(
      PasswordEncoder encoder,
      PasswordPolicy policy,
      EmailService email,
      UserAccountService accounts,
      CustomUserDetailsService details,
      RateLimits limits) {
    this.encoder = encoder;
    this.policy = policy;
    this.email = email;
    this.accounts = accounts;
    this.details = details;
    this.limits = limits;
  }

  private boolean signedIn() {
    var a = SecurityContextHolder.getContext().getAuthentication();
    return a != null && a.isAuthenticated() && !(a instanceof AnonymousAuthenticationToken);
  }

  @GetMapping("/registro")
  public String mostrarRegistro() {
    return signedIn() ? "redirect:/" : "Registro";
  }

  @PostMapping("/registro/enviar")
  public String procesarRegistro(
      @Valid @ModelAttribute("registro") RegistroRequest form,
      BindingResult errors,
      HttpServletRequest req,
      RedirectAttributes flash) {
    if (signedIn()) return "redirect:/";
    if (errors.hasErrors()) {
      flash.addFlashAttribute("error", "Revisa los datos del formulario.");
      return "redirect:/registro";
    }
    try {
      limits.check("register-ip", req.getRemoteAddr(), 5, Duration.ofMinutes(15));
      String correo = UserAccountService.normalizeEmail(form.correoElectronico());
      limits.check("register-account", correo, 3, Duration.ofMinutes(15));
      policy.validate(form.password());
      String code = TokenTools.code();
      //Ahorita
      Instant ahora =Instant.now();
      Pending pending =
          new Pending(
              form.nombres(),
              form.apellidos(),
              form.telefono(),
              correo,
              encoder.encode(form.password()),
              TokenTools.hash(code),
              Instant.now().plus(Duration.ofMinutes(10)),
              0,
              LegalVersions.TERMINOS,
              LegalVersions.PRIVACIDAD,
              ahora);
      email.enviarCorreoVerificacion(correo, code, form.nombres() + " " + form.apellidos());
      req.getSession().setAttribute("pendingRegistration", pending);
      return "redirect:/verificar";
    } catch (BusinessException e) {
      flash.addFlashAttribute("error", e.getMessage());
      return "redirect:/registro";
    }
  }

  @GetMapping("/verificar")
  public String mostrarVerificacion(HttpSession session) {
    return signedIn()
        ? "redirect:/"
        : session.getAttribute("pendingRegistration") == null ? "redirect:/registro" : "Verificar";
  }

  @PostMapping("/verificar/confirmar")
  public String confirmarCodigo(
      @RequestParam String codigoIngresado,
      HttpServletRequest req,
      HttpServletResponse res,
      Model model) {
    if (signedIn()) return "redirect:/";
    limits.check("verify-ip", req.getRemoteAddr(), 30, Duration.ofMinutes(15));
    HttpSession session = req.getSession();
    synchronized (session) {
      Pending p = (Pending) session.getAttribute("pendingRegistration");
      if (p == null || p.expires().isBefore(Instant.now()) || p.attempts() >= 5) {
        session.removeAttribute("pendingRegistration");
        model.addAttribute(
            "error", "El código venció o agotó sus intentos. Inicia el registro nuevamente.");
        return "Verificar";
      }
      session.setAttribute(
          "pendingRegistration",
          new Pending(
              p.nombres(),
              p.apellidos(),
              p.telefono(),
              p.correo(),
              p.passwordHash(),
              p.codeHash(),
              p.expires(),
              p.attempts() + 1,
              p.versionTerminos(),
              p.versionPrivacidad(),
              p.fechaAceptacionLegal())
          );
      if (!codigoIngresado.matches("[0-9]{6}")
          || !TokenTools.matches(codigoIngresado, p.codeHash())) {
        model.addAttribute("error", "Código incorrecto.");
        return "Verificar";
      }
      try {
        accounts.createClient(
            p.nombres(), p.apellidos(), p.telefono(), p.correo(), p.passwordHash(),
            p.versionTerminos(),
            p.versionPrivacidad(),
            p.fechaAceptacionLegal());
      } catch (BusinessException | org.springframework.dao.DataIntegrityViolationException e) {
        session.removeAttribute("pendingRegistration");
        model.addAttribute("error", "No se pudo completar el registro con esos datos.");
        return "Verificar";
      }
      session.removeAttribute("pendingRegistration");
      req.changeSessionId();
      var user = details.loadUserByUsername(p.correo());
      var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
      new CsrfAuthenticationStrategy(new HttpSessionCsrfTokenRepository())
          .onAuthentication(auth, req, res);
      var context = SecurityContextHolder.createEmptyContext();
      context.setAuthentication(auth);
      SecurityContextHolder.setContext(context);
      new HttpSessionSecurityContextRepository().saveContext(context, req, res);
      return "redirect:/";
    }
  }
}
