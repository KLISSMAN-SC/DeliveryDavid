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
        
        // Listas base para los botones
        model.addAttribute("listaTipos", tipoNegocioService.obtenerTodos());
        model.addAttribute("listaZonas", zonaService.obtenerTodas());

        // Ejecutar consulta maestra con los filtros (si son nulos, la BD los ignora)
        List<Negocio> negociosMostrar = negocioService.buscarConFiltrosCombinados(tipoId, zonaId, categoria);
        
     // Dentro de tu @GetMapping("/")
        List<Promocion> promocionesIndex = promocionRepo.findAll().stream()
                .filter(p -> p.getMostrarEnIndex() != null && p.getMostrarEnIndex()) // Seleccionadas por el admin
                .filter(Promocion::isActivaHoy) // Magia: Solo pasan las que cumplen fecha, hora y día
                .collect(Collectors.toList());
                
        model.addAttribute("promocionesCarrusel", promocionesIndex);

        
        boolean esRestaurante = true; // Por defecto asumimos restaurante
        String tituloSeccion = "Restaurantes cerca de ti";

        if (tipoId != null) {
            TipoNegocio tipoSel = tipoNegocioService.obtenerPorId(tipoId);
            if (tipoSel != null) {
                tituloSeccion = tipoSel.getNombre() + " cerca de ti";
                // Si NO contiene la palabra restaurante/comida, ocultamos la sección de categorías
                if (!tipoSel.getNombre().toLowerCase().contains("restaurante")) {
                    esRestaurante = false;
                }
            }
        }

        // Enviamos las listas y los identificadores seleccionados a la vista
        model.addAttribute("listaNegocios", negociosMostrar);
        model.addAttribute("esRestaurante", esRestaurante);
        model.addAttribute("tituloSeccion", tituloSeccion);
        
        // Mantenemos el estado actual de los filtros
        model.addAttribute("tipoSeleccionado", tipoId);
        model.addAttribute("zonaSeleccionada", zonaId);
        model.addAttribute("categoriaSeleccionada", categoria);

        // Si es restaurante, enviamos las categorías únicas
        if (esRestaurante) {
            model.addAttribute("listaCategorias", categoriaProductoService.obtenerCategoriasUnicas());
        }

        return "Index"; 
    }
}