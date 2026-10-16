package com.proyecto.controller;

import com.proyecto.security.*;
import com.proyecto.services.UserAccountService;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PasswordController {
  private final UserAccountService accounts;
  private final RateLimits limits;
  private final SecurityAudit audit;

  public PasswordController(UserAccountService accounts, RateLimits limits, SecurityAudit audit) {
    this.accounts = accounts;
    this.limits = limits;
    this.audit = audit;
  }

  @GetMapping("/recuperar-password")
  public String mostrarFormularioRecuperacion() {
    return "recuperar-password";
  }

  @PostMapping("/recuperar-password")
  public String procesarRecuperacion(
      @RequestParam String correo, HttpServletRequest req, RedirectAttributes flash) {
    limits.check("recovery-ip", req.getRemoteAddr(), 5, Duration.ofMinutes(15));
    limits.check(
        "recovery-account", UserAccountService.normalizeEmail(correo), 3, Duration.ofMinutes(15));
    if (correo.length() <= 45)
      try {
        accounts.requestReset(correo);
      } catch (org.springframework.core.task.TaskRejectedException e) {
        audit.alert("MAIL_QUEUE_FULL", "anonymous");
      }
    flash.addFlashAttribute("exito", "Si el correo está registrado, recibirás las instrucciones.");
    return "redirect:/recuperar-password";
  }

  @GetMapping("/recuperar-password/reset")
  public String mostrarFormularioReset(@RequestParam String token, Model model) {
    if (!accounts.validReset(token)) return "redirect:/login?errorToken=true";
    model.addAttribute("token", token);
    return "reset-password";
  }

  @PostMapping("/recuperar-password/reset")
  public String guardarNuevaPassword(
      @RequestParam String token,
      @RequestParam String password,
      HttpServletRequest req,
      Model model,
      RedirectAttributes flash) {
    limits.check("reset-ip", req.getRemoteAddr(), 10, Duration.ofMinutes(15));
    try {
      accounts.reset(token, password);
    } catch (BusinessException e) {
      model.addAttribute("token", token);
      model.addAttribute("error", e.getMessage());
      return "reset-password";
    }
    var session = req.getSession(false);
    if (session != null) session.invalidate();
    org.springframework.security.core.context.SecurityContextHolder.clearContext();
    flash.addFlashAttribute("exitoReseteo", "Contraseña actualizada. Inicia sesión nuevamente.");
    return "redirect:/login";
  }
}
