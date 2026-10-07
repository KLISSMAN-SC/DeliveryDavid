package com.proyecto.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.repository.DireccionUsuarioRepository;
import com.proyecto.model.DireccionUsuario;

@Service
public class DireccionUsuarioService {
	 
	@Autowired
	private DireccionUsuarioRepository direccionUsuarioRepository;


	    public List<DireccionUsuario>
	            listarPorUsuario(Integer idUsuario) {

	        return direccionUsuarioRepository.findByUsuarioIdUsuarioOrderByPrincipalDescIdDireccionUsuarioAsc(idUsuario);
	    }


	    public DireccionUsuario obtenerDireccionUsuario(Integer idDireccionUsuario,Integer idUsuario) {

	        return direccionUsuarioRepository.findByIdDireccionUsuarioAndUsuarioIdUsuario(idDireccionUsuario,idUsuario).orElseThrow(() ->
	                    new RuntimeException("La dirección no pertenece al usuario"));
	    }

	    public DireccionUsuario obtenerPrincipalOPrimera(Integer idUsuario) {

	        List<DireccionUsuario> direcciones =listarPorUsuario(idUsuario);

	        if (direcciones.isEmpty()) {
	            return null;
	        }

	        // Como el Repository ordena
	        // principal DESC, la primera
	        // será la principal.
	        return direcciones.get(0);
	    }
	}
