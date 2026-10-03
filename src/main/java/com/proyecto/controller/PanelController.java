package com.proyecto.controller;

import com.proyecto.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/panel")
public class PanelController {

    @Autowired private UsuarioRepository usuarioRepo;
    @Autowired private TipoNegocioRepository tipoNegocioRepo;
    @Autowired private NegocioRepository negocioRepo;
    @Autowired private CategoriaProductoRepository categoriaRepo;
    @Autowired private PromocionRepository promocionRepo;
    @Autowired private PedidoRepository pedidoRepo;
    @Autowired private ProductoRepository productoRepo;

    @GetMapping
    public String mostrarPanel(@RequestParam(name = "modulo", required = false) String modulo, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean esAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));

        if (modulo == null || modulo.isEmpty()) {
            return esAdmin ? "redirect:/panel?modulo=usuarios" : "redirect:/panel?modulo=pedidos";
        }

        if (!esAdmin && !modulo.equals("pedidos")) {
            return "redirect:/panel?modulo=pedidos";
        }

        model.addAttribute("modulo", modulo);

        // Consultar la base de datos dinámicamente según el módulo seleccionado
        switch (modulo) {
            case "usuarios":
                model.addAttribute("listaUsuarios", usuarioRepo.findAll());
                break;
            case "tipos_negocio":
                model.addAttribute("listaTipos", tipoNegocioRepo.findAll());
                break;
            case "negocios":
                model.addAttribute("listaNegocios", negocioRepo.findAll());
                break;
            case "categorias":
                model.addAttribute("listaCategorias", categoriaRepo.findAll());
                break;
            case "productos": 
                model.addAttribute("listaProductos", productoRepo.findAll());
                break;
            case "promociones":
                model.addAttribute("listaPromociones", promocionRepo.findAll());
                break;
            case "pedidos":
                model.addAttribute("listaPedidos", pedidoRepo.findAll());
                break;
        }

        return "Panel";
    }
}