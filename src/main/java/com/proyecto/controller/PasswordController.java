package com.proyecto.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.proyecto.model.Usuario;
import com.proyecto.repository.UsuarioRepository;
import com.proyecto.services.EmailService;

@Controller
public class PasswordController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;

    // Inyectamos el encriptador para guardar la nueva contraseña segura
    @Autowired
    private PasswordEncoder passwordEncoder;

    // 1. Mostrar pantalla de pedir correo
    @GetMapping("/recuperar-password")
    public String mostrarFormularioRecuperacion() {
        return "recuperar-password"; 
    }

    // 2. Procesar el envío del correo
    @PostMapping("/recuperar-password")
    public String procesarRecuperacion(@RequestParam("correo") String correo, RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioRepository.findByCorreoElectronico(correo).orElse(null);

        if (usuario != null) {
            String token = UUID.randomUUID().toString();
            
            // Guardamos el token en la base de datos
            usuario.setTokenPassword(token);
            usuarioRepository.save(usuario);

            try {
                // Obtenemos los nombres y apellidos del usuario para personalizar el correo
                String nombreCompleto = usuario.getNombres() + " " + usuario.getApellidos();
                
                // Pasamos el correo, el token y el nombre completo
                emailService.enviarCorreoRecuperacion(usuario.getCorreoElectronico(), token, nombreCompleto);
                
                redirectAttributes.addFlashAttribute("exito", "Si el correo existe, se enviaron las instrucciones.");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Hubo un problema al enviar el correo. Intenta de nuevo.");
            }
        } else {
            redirectAttributes.addFlashAttribute("exito", "Si el correo existe, se enviaron las instrucciones.");
        }
        return "redirect:/recuperar-password";
    }

    // 3. Cuando el usuario hace clic en el enlace del correo
    @GetMapping("/recuperar-password/reset")
    public String mostrarFormularioReset(@RequestParam("token") String token, Model model) {
        // Verificamos si el token existe en la BD
        Usuario usuario = usuarioRepository.findByTokenPassword(token).orElse(null);
        
        if (usuario == null) {
            // Si el token es falso o ya se usó, lo mandamos al login con error
            return "redirect:/login?errorToken=true";
        }
        
        // Pasamos el token a la vista para que el formulario sepa a quién cambiarle la clave
        model.addAttribute("token", token);
        return "reset-password";
    }

    // 4. Guardar la nueva contraseña
    @PostMapping("/recuperar-password/reset")
    public String guardarNuevaPassword(@RequestParam("token") String token, 
                                       @RequestParam("password") String nuevaPassword,
                                       RedirectAttributes redirectAttributes) {
        
        Usuario usuario = usuarioRepository.findByTokenPassword(token).orElse(null);
        
        if (usuario == null) {
            return "redirect:/login?errorToken=true";
        }

        // Encriptamos la nueva contraseña y la guardamos
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        // Borramos el token para que no se pueda volver a usar ese enlace
        usuario.setTokenPassword(null);
        usuarioRepository.save(usuario);
        
        redirectAttributes.addFlashAttribute("exitoReseteo", "Tu contraseña ha sido actualizada. Ya puedes iniciar sesión.");
        return "redirect:/login";
    }
}