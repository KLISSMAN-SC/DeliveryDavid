package com.proyecto.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.proyecto.model.Usuario;
import com.proyecto.repository.UsuarioRepository;

@Service
public class UsuarioService {
	
	@Autowired 
	private UsuarioRepository usuarioRepository;
	
	public Usuario obtenerUsuarioAutenticado(
	        Authentication authentication) {


	    if (
	        authentication == null ||
	        !authentication.isAuthenticated()
	    ) {

	        throw new RuntimeException(
	            "Usuario no autenticado."
	        );
	    }


	    String correo =
	            authentication.getName();


	    return usuarioRepository
	            .findByCorreoElectronico(
	                correo
	            )
	            .orElseThrow(
	                () -> new RuntimeException(
	                    "Usuario autenticado no encontrado."
	                )
	            );
}
}
