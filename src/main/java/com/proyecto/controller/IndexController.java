package com.proyecto.controller;

import com.proyecto.services.TipoNegocioService;
import com.proyecto.services.ZonaService;
import com.proyecto.services.CategoriaProductoService;
import com.proyecto.services.NegocioService; // <-- Importar
import com.proyecto.model.*;
import com.proyecto.repository.PromocionRepository;

import java.util.List;
import java.util.stream.Collectors;

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
    
    @Autowired private PromocionRepository promocionRepo;
    
    @Autowired
    private NegocioService negocioService; // <-- Inyectar
    
    @GetMapping("/")
    public String paginaPrincipal(
            @RequestParam(required = false) Integer tipoId,
            @RequestParam(required = false) Integer zonaId,
            @RequestParam(required = false) String categoria,
            Model model) {
        
        // 1. Filtrar los botones superiores (Tipos de Negocio)
        List<TipoNegocio> tiposActivos = tipoNegocioService.obtenerTodos().stream()
                .filter(t -> t.getEstado() != null && t.getEstado())
                .collect(Collectors.toList());
        model.addAttribute("listaTipos", tiposActivos);
        
        model.addAttribute("listaZonas", zonaService.obtenerTodas());

        // Ejecutar consulta maestra de negocios
        List<Negocio> negociosMostrar = negocioService.buscarConFiltrosCombinados(tipoId, zonaId, categoria);
        
        // 2. Filtrar negocios cuyas categorías están ocultas
        List<Negocio> negociosVisibles = negociosMostrar.stream()
                .filter(n -> n.getTipoNegocio() != null 
                          && n.getTipoNegocio().getEstado() != null 
                          && n.getTipoNegocio().getEstado())
                .collect(Collectors.toList());
        
        List<Promocion> promocionesIndex = promocionRepo.findAll().stream()
                .filter(p -> p.getMostrarEnIndex() != null && p.getMostrarEnIndex()) 
                .filter(Promocion::isActivaHoy) 
                .collect(Collectors.toList());
                
        model.addAttribute("promocionesCarrusel", promocionesIndex);

        boolean esRestaurante = true; 
        String tituloSeccion = "Restaurantes cerca de ti";

        if (tipoId != null) {
            TipoNegocio tipoSel = tipoNegocioService.obtenerPorId(tipoId);
            if (tipoSel != null) {
                tituloSeccion = tipoSel.getNombre() + " cerca de ti";
                if (!tipoSel.getNombre().toLowerCase().contains("restaurante")) {
                    esRestaurante = false;
                }
            }
        }

        // Enviamos las listas filtradas a la vista
        model.addAttribute("listaNegocios", negociosVisibles); // <--- Variable actualizada
        model.addAttribute("esRestaurante", esRestaurante);
        model.addAttribute("tituloSeccion", tituloSeccion);
        
        model.addAttribute("tipoSeleccionado", tipoId);
        model.addAttribute("zonaSeleccionada", zonaId);
        model.addAttribute("categoriaSeleccionada", categoria);

        if (esRestaurante) {
            model.addAttribute("listaCategorias", categoriaProductoService.obtenerCategoriasUnicas());
        }

        return "Index"; 
    }
}