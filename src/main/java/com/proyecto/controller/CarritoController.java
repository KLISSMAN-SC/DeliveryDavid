package com.proyecto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.model.Carrito;
import com.proyecto.services.CarritoService;

@RestController

public class CarritoController {

	@Autowired
    private CarritoService carritoService;


	@PostMapping("/carrito/agregar")
    public Carrito agregarProducto(
            @RequestParam Integer idUsuario,
            @RequestParam Integer idNegocio,
            @RequestParam Integer idProducto) {
        return carritoService.agregarProducto(
                idUsuario,
                idNegocio,
                idProducto
        );
    }
	@PostMapping("/carrito/disminuir")
	public Carrito disminuirProducto(
	        @RequestParam Integer idUsuario,
	        @RequestParam Integer idNegocio,
	        @RequestParam Integer idProducto) {

	    return carritoService.disminuirProducto(
	            idUsuario,
	            idNegocio,
	            idProducto
	    );
	}
	@DeleteMapping("/carrito/eliminar")
	public Carrito eliminarProducto(
	        @RequestParam Integer idUsuario,
	        @RequestParam Integer idNegocio,
	        @RequestParam Integer idProducto) {

	    return carritoService.eliminarProducto(
	            idUsuario,
	            idNegocio,
	            idProducto
	    );
	}


    // =====================================================
    // OBTENER CARRITO ACTUAL
    // =====================================================

    @GetMapping("/carrito/actual")
    public Carrito obtenerCarritoActual(
            @RequestParam Integer idUsuario,
            @RequestParam Integer idNegocio) {

        return carritoService.obtenerCarritoActual(
                idUsuario,
                idNegocio
        );
    }
}
