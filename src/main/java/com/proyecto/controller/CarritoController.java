package com.proyecto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.model.Carrito;
import com.proyecto.model.Usuario;
import com.proyecto.services.CarritoService;
import com.proyecto.services.UsuarioService;

@RestController

public class CarritoController {

	@Autowired
    private CarritoService carritoService;
	//HOLA
	@Autowired
	private UsuarioService usuarioService;
	
	@PostMapping("/carrito/agregar")
    public Carrito agregarProducto(
    		Authentication authentication,
            @RequestParam Integer idNegocio,
            @RequestParam Integer idProducto) {
		Usuario usuario=usuarioService.obtenerUsuarioAutenticado(authentication);
        return carritoService.agregarProducto(
                usuario.getIdUsuario(),
                idNegocio,
                idProducto
        );
    }
	@PostMapping("/carrito/disminuir")
	public Carrito disminuirProducto(
			Authentication authentication,
	        @RequestParam Integer idNegocio,
	        @RequestParam Integer idProducto) {
		Usuario usuario=usuarioService.obtenerUsuarioAutenticado(authentication);
	    return carritoService.disminuirProducto(
	            usuario.getIdUsuario(),
	            idNegocio,
	            idProducto
	    );
	}
	@DeleteMapping("/carrito/eliminar")
	public Carrito eliminarProducto(
			Authentication authentication,
	        @RequestParam Integer idNegocio,
	        @RequestParam Integer idProducto) {
		Usuario usuario=usuarioService.obtenerUsuarioAutenticado(authentication);
	    return carritoService.eliminarProducto(
	            usuario.getIdUsuario(),
	            idNegocio,
	            idProducto
	    );
	}


    // =====================================================
    // OBTENER CARRITO ACTUAL
    // =====================================================

    @GetMapping("/carrito/actual")
    public Carrito obtenerCarritoActual(
    		Authentication authentication,
            @RequestParam Integer idNegocio) {
    	Usuario usuario=usuarioService.obtenerUsuarioAutenticado(authentication);
        return carritoService.obtenerCarritoActual(
                usuario.getIdUsuario(),
                idNegocio
        );
    }
}
