package com.proyecto.controller;

import com.proyecto.dto.PerfilRequest;
import com.proyecto.repository.UsuarioRepository;
import com.proyecto.security.BusinessException;
import com.proyecto.services.UserAccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/perfil")
public class PerfilController {
  private final UsuarioRepository users;
  private final UserAccountService accounts;

  public PerfilController(UsuarioRepository users, UserAccountService accounts) {
    this.users = users;
    this.accounts = accounts;
  }

  @GetMapping
  public String mostrarPerfil(Authentication auth, Model model) {
    model.addAttribute(
        "usuario",
        users.findByCorreoElectronico(auth.getName()).orElseThrow(BusinessException::missing));
    return "Perfil";
  }

  @PostMapping("/actualizar")
  public String actualizarPerfil(
      @Valid @ModelAttribute PerfilRequest form,
      BindingResult errors,
      Authentication auth,
      HttpServletRequest req,
      RedirectAttributes flash) {
    if (errors.hasErrors()) {
      flash.addFlashAttribute("mensajeError", "Revisa los datos del formulario.");
      return "redirect:/perfil";
    }
    try {
      boolean changed = accounts.updateProfile(auth.getName(), form);
      if (changed) {
        req.getSession().invalidate();
        SecurityContextHolder.clearContext();
        return "redirect:/login?passwordActualizada=true";
      }
    } catch (BusinessException | org.springframework.dao.DataIntegrityViolationException e) {
      flash.addFlashAttribute(
          "mensajeError",
          e instanceof BusinessException ? e.getMessage() : "Los datos ya están registrados.");
      return "redirect:/perfil";
    }
    flash.addFlashAttribute("mensajeExito", "Tus datos han sido actualizados.");
    return "redirect:/perfil";
  }
}
