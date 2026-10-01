package com.proyecto.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.proyecto.model.Negocio;
import com.proyecto.model.Producto;
import com.proyecto.services.NegocioService;
import com.proyecto.services.ProductoService;

@Controller
public class ProductoController {
	
	@Autowired
	private ProductoService productoService;
	
	@Autowired 
	private NegocioService negocioService;
	
	@GetMapping("/restaurante/{id}")
	public String verRestaurante(
	        @PathVariable Integer id,
	        Model model){


	    Negocio negocio =
	        negocioService.obtenerporId(id);


	    List<Producto> productos =
	        productoService.listarProductosPorNegocio(id);


	    model.addAttribute("negocio", negocio);
	    model.addAttribute("productos", productos);


	    return "fragmentos/restaurante :: vista";

	}
	@GetMapping("/productos/buscar")
	public String buscarProductos(
	        @RequestParam Integer idNegocio,
	        @RequestParam(required = false, defaultValue = "") String q,
	        Model model) {

	    List<Producto> productos =
	            productoService.buscarProductosPorNegocio(
	                    idNegocio,
	                    q
	            );


	    Negocio negocio =
		        negocioService.obtenerporId(idNegocio);


	    model.addAttribute("productos", productos);
	    model.addAttribute("negocio", negocio);


	    return "fragmentos/restaurante :: listaProductos";
	}
}
