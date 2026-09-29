package com.proyecto.controller;

import com.proyecto.model.Negocio;
import com.proyecto.services.NegocioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;

@RestController
public class NegocioController {

    @Autowired
    private NegocioService negocioService;

    @GetMapping("/negocios/buscar")
    public List<Map<String, Object>> buscar(@RequestParam("q") String q) {
        List<Negocio> negocios = negocioService.buscarPorNombre(q);
        return negocios.stream().map(n -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", n.getIdNegocio());
            map.put("nombre", n.getNombre());
            map.put("direccion", n.getDireccion());
            map.put("estado", n.getEstado());
            map.put("tipo", n.getTipoNegocio() != null ? n.getTipoNegocio().getNombre() : "");
            return map;
        }).collect(Collectors.toList());
    }
}