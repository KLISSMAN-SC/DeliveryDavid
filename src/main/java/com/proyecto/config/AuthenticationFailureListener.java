package com.proyecto.config;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import com.proyecto.model.Usuario;
import com.proyecto.repository.UsuarioRepository; // Asegúrate de tener e importar tu repositorio

@Component
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        // Obtenemos el correo que intentaron usar
        String correo = (String) event.getAuthentication().getPrincipal();

        Usuario usuario = usuarioRepository.findByCorreoElectronico(correo).orElse(null); // Ajusta el nombre del método según tu repositorio

        if (usuario != null) {
            // Si el usuario existe, incrementamos sus fallos
            int intentos = (usuario.getIntentosFallidos() == null) ? 0 : usuario.getIntentosFallidos();
            intentos++;

            usuario.setIntentosFallidos(intentos);

            // Si llega a 5 intentos, lo bloqueamos
            if (intentos >= 5) {
                usuario.setFechaBloqueo(LocalDateTime.now());
            }

            usuarioRepository.save(usuario);
        }
    }
}