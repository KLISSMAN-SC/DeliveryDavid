package com.proyecto.controller;

import com.proyecto.services.TipoNegocioService;
import com.proyecto.services.ZonaService;
import com.proyecto.services.CategoriaProductoService;
import com.proyecto.services.NegocioService; // <-- Importar
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @Autowired
    private TipoNegocioService tipoNegocioService;
    
    @Autowired
    private ZonaService zonaService;
    
    @Autowired
    private CategoriaProductoService categoriaProductoService;
    
    @Autowired
    private NegocioService negocioService; // <-- Inyectar

    @GetMapping("/")
    public String paginaPrincipal(@RequestParam(required = false) Long tipoId, Model model) {
        
        // Enviamos las 4 listas a la plantilla HTML
        model.addAttribute("listaTipos", tipoNegocioService.obtenerTodos());
        model.addAttribute("listaZonas", zonaService.obtenerTodas());
     // Cambia la línea de listaCategorias a esto:
        model.addAttribute("listaCategorias", categoriaProductoService.obtenerCategoriasUnicas());
        model.addAttribute("listaNegocios", negocioService.obtenerTodos()); // <-- Nueva lista
        
        return "Index"; 
    }
}