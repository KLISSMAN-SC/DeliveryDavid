package com.proyecto.controller;

import com.proyecto.model.Rol;
import com.proyecto.model.Usuario;
import com.proyecto.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import com.proyecto.services.CustomUserDetailsService;	

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import java.util.Random;

@Controller
public class RegistroController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private CustomUserDetailsService userDetailsService;

    // 1. Mostrar formulario de registro (Protegido)
    @GetMapping("/registro")
    public String mostrarRegistro() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/"; // Lo expulsamos de vuelta al inicio
        }
        
        return "Registro";
    }

    // 2. Procesar datos, generar código y enviar correo
    @PostMapping("/registro/enviar")
    public String procesarRegistro(Usuario usuario, HttpSession session) {
        
        // Comprobar si el correo ya existe en BD
        if(usuarioRepository.findByCorreoElectronico(usuario.getCorreoElectronico()).isPresent()){
            return "redirect:/registro?error=CorreoYaExiste";
        }

        // Generar código de 6 dígitos
        String codigo = String.format("%06d", new Random().nextInt(999999));
        
        // Encriptar la contraseña introducida por el usuario
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        
        // Asignar el Rol de Cliente (Asumiendo que el ID 2 en tu tabla ROL es 'Cliente')
        Rol rolCliente = new Rol();
        rolCliente.setIdRol(2);
        rolCliente.setNombre("CLIENTE");
        usuario.setRol(rolCliente);

        // Guardar temporalmente en sesión
        session.setAttribute("usuarioTemp", usuario);
        session.setAttribute("codigoVerificacion", codigo);

        // Enviar el correo
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(usuario.getCorreoElectronico());
        mensaje.setSubject("Código de Verificación - RIDE MEAL");
        mensaje.setText("Hola " + usuario.getNombres() + ",\n\nTu código de verificación es: " + codigo + "\n\nBienvenido a RIDE MEAL.");
        mailSender.send(mensaje);

        return "redirect:/verificar";
    }

    // 3. Mostrar pantalla de verificación
    @GetMapping("/verificar")
    public String mostrarVerificacion(HttpSession session) {
        if(session.getAttribute("usuarioTemp") == null) {
            return "redirect:/registro";
        }
        return "Verificar";
    }

 // 4. Confirmar el código, guardar en BD e Iniciar Sesión automáticamente
    @PostMapping("/verificar/confirmar")
    public String confirmarCodigo(@RequestParam String codigoIngresado, HttpSession session, Model model) {
        String codigoReal = (String) session.getAttribute("codigoVerificacion");
        Usuario usuario = (Usuario) session.getAttribute("usuarioTemp");

        if(codigoReal != null && codigoReal.equals(codigoIngresado)) {
            
            // 1. Guardar usuario en BD definitivamente
            usuarioRepository.save(usuario);
            
            // 2. INICIAR SESIÓN AUTOMÁTICAMENTE
            // Cargamos los datos del usuario recién creado para Spring Security
            UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getCorreoElectronico());
            
            // Creamos el token de autenticación
            UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            
            // Lo guardamos en el contexto de seguridad
            SecurityContextHolder.getContext().setAuthentication(authToken);
            
            // Aseguramos que la sesión persista en el navegador
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, 
                               SecurityContextHolder.getContext());
            
            // 3. Limpiar las variables temporales
            session.removeAttribute("usuarioTemp");
            session.removeAttribute("codigoVerificacion");
            
            // 4. Redirigir a la pantalla principal
            return "redirect:/";
            
        } else {
            model.addAttribute("error", "Código incorrecto. Inténtalo de nuevo.");
            return "Verificar";
        }
    }
}