package com.proyecto.services;

import com.proyecto.model.Usuario;
import com.proyecto.repository.UsuarioRepository;
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
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreoElectronico(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Extraemos el nombre del rol de la BD (Ej: "ADMINISTRADOR", "REPARTIDOR", "CLIENTE")
        String nombreRol = usuario.getRol().getNombre().toUpperCase();

        return User.builder()
                .username(usuario.getCorreoElectronico())
                .password(usuario.getPassword())
                .roles(nombreRol) // Spring Security requiere los roles en mayúsculas
                .build();
    }
}