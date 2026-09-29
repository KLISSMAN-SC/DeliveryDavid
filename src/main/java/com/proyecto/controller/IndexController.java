package com.proyecto.controller;

import com.proyecto.services.TipoNegocioService;
import com.proyecto.services.ZonaService;
import com.proyecto.services.CategoriaProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @Autowired
    private TipoNegocioService tipoNegocioService;
    
    @Autowired
    private ZonaService zonaService;
    
    @Autowired
    private CategoriaProductoService categoriaProductoService;

    @GetMapping("/")
    public String paginaPrincipal(Model model) {
        
        // Enviamos las 3 listas a la plantilla HTML
        model.addAttribute("listaTipos", tipoNegocioService.obtenerTodos());
        model.addAttribute("listaZonas", zonaService.obtenerTodas());
        model.addAttribute("listaCategorias", categoriaProductoService.obtenerTodas());
        
        return "Index"; 
    }
}