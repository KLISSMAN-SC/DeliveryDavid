package com.proyecto.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import com.proyecto.model.Usuario;
import com.proyecto.repository.UsuarioRepository;

@Component
public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        User userDetails = (User) event.getAuthentication().getPrincipal();
        
        // AQUÍ ESTÁ LA CORRECCIÓN
        Usuario usuario = usuarioRepository.findByCorreoElectronico(userDetails.getUsername()).orElse(null);

        if (usuario != null) {
            if (usuario.getIntentosFallidos() != null && usuario.getIntentosFallidos() > 0) {
                usuario.setIntentosFallidos(0);
                usuario.setFechaBloqueo(null);
                usuarioRepository.save(usuario);
            }
        }
    }
}