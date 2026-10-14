package com.proyecto.services;

import com.proyecto.model.Usuario;
import com.proyecto.repository.UsuarioRepository;

// IMPORT CORREGIDO: Usando la clase nativa de Java
import java.time.Duration; 
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        // Busca, y si el Optional está vacío, lanza la excepción de una vez
        Usuario usuario = usuarioRepository.findByCorreoElectronico(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // ==========================================
        // VALIDACIÓN DE BLOQUEO (Las 5 horas)
        // ==========================================
        if (usuario.getFechaBloqueo() != null) {
            LocalDateTime ahora = LocalDateTime.now();
            Duration tiempoTranscurrido = Duration.between(usuario.getFechaBloqueo(), ahora);

            if (tiempoTranscurrido.toHours() < 5) {
                long horasRestantes = 5 - tiempoTranscurrido.toHours();
                throw new RuntimeException("CUENTA_BLOQUEADA:" + horasRestantes);
            } else {
                usuario.setIntentosFallidos(0);
                usuario.setFechaBloqueo(null);
                usuarioRepository.save(usuario);
            }
        }

        return User.builder()
                .username(usuario.getCorreoElectronico())
                .password(usuario.getPassword())
                .roles(obtenerNombreRol(usuario.getRol().getIdRol()))
                .build();
    }
    
    // MÉTODO AUXILIAR AÑADIDO (Este era el que marcaba error en la línea 50)
    private String obtenerNombreRol(Integer idRol) {
        if(idRol == 1) return "ADMINISTRADOR";
        if(idRol == 2) return "CLIENTE";
        if(idRol == 3) return "REPARTIDOR";
        return "USUARIO";
    }
}