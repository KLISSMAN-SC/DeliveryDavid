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
        
    
    	// 1. Buscamos al usuario por su correo en la BD
        Usuario usuario = usuarioRepository.findByCorreoElectronico(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario o contraseña incorrectos"));
        
        
       
        // 2. Construimos el usuario validado para Spring Security
        return User.builder()
                .username(usuario.getCorreoElectronico())
                .password(usuario.getPassword()) // Spring Security comparará automáticamente el BCrypt
                .roles("USER") // Asignamos un rol genérico por ahora
                .build();
    }
}