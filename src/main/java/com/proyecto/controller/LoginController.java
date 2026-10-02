package com.proyecto.controller;

import com.proyecto.model.Usuario;
import com.proyecto.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.stereotype.Controller;

@Controller
@ControllerAdvice
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;


    // ==========================================
    // MOSTRAR LOGIN
    // ==========================================
    @GetMapping("/login")
    public String mostrarLogin() {
        // Obtenemos los datos de la sesión actual
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        // Si la sesión existe, está autenticada y NO es un visitante anónimo (invitado)
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/"; // Lo expulsamos de vuelta al inicio
        }
        
        return "Login"; 
    }


    // ==========================================
    // USUARIO AUTENTICADO DISPONIBLE EN THYMELEAF
    // ==========================================
    @ModelAttribute
    public void agregarUsuarioAutenticado(
            Authentication authentication,
            Model model) {

        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {

            String correo = authentication.getName();

            Usuario usuario = usuarioRepository
                    .findByCorreoElectronico(correo)
                    .orElse(null);

            model.addAttribute("usuarioAutenticado", usuario);

        } else {

            model.addAttribute("usuarioAutenticado", null);
        }
    }
}