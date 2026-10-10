package com.proyecto.controller;

import com.proyecto.model.Usuario;
import com.proyecto.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 1. Mostrar la vista del perfil
    @GetMapping
    public String mostrarPerfil(Model model) {
        // Obtener la sesión del usuario actual
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String correoActual = auth.getName(); 
        
        // Buscar al usuario en la base de datos
        Usuario usuario = usuarioRepo.findByCorreoElectronico(correoActual).orElse(null);
        
        if (usuario == null) {
            return "redirect:/login"; // Por seguridad, si no existe, lo manda al login
        }
        
        model.addAttribute("usuario", usuario);
        return "Perfil"; // Retorna el archivo Perfil.html
    }

   
 // 2. Guardar los cambios del perfil
    @PostMapping("/actualizar")
    public String actualizarPerfil(@ModelAttribute Usuario datosFormulario, RedirectAttributes redirectAttributes) {
        
        // Obtenemos al usuario directamente desde la sesión por seguridad
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String correoActual = auth.getName();
        Usuario usuarioLogueado = usuarioRepo.findByCorreoElectronico(correoActual).orElse(null);
        
        if (usuarioLogueado == null) {
            return "redirect:/login";
        }

        // Validar que el Teléfono no esté duplicado en otra cuenta
        if (!usuarioLogueado.getTelefono().equals(datosFormulario.getTelefono())) {
            Usuario existeTel = usuarioRepo.findByTelefono(datosFormulario.getTelefono()).orElse(null);
            if (existeTel != null) {
                redirectAttributes.addFlashAttribute("mensajeError", "El teléfono ya está registrado en otra cuenta.");
                return "redirect:/perfil";
            }
        }

        // Actualizamos estrictamente solo los datos permitidos (Sin DNI ni Correo)
        usuarioLogueado.setNombres(datosFormulario.getNombres());
        usuarioLogueado.setApellidos(datosFormulario.getApellidos());
        usuarioLogueado.setTelefono(datosFormulario.getTelefono());

        // Manejo de la contraseña: Solo se encripta y actualiza si el usuario escribió algo
        if (datosFormulario.getPassword() != null && !datosFormulario.getPassword().trim().isEmpty()) {
            usuarioLogueado.setPassword(passwordEncoder.encode(datosFormulario.getPassword()));
        }

        // Guardamos en la base de datos
        usuarioRepo.save(usuarioLogueado);
        
        redirectAttributes.addFlashAttribute("mensajeExito", "Tus datos han sido actualizados correctamente.");
        return "redirect:/perfil";
    }
}