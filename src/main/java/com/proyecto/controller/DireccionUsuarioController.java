package com.proyecto.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proyecto.model.DireccionUsuario;
import com.proyecto.services.DireccionUsuarioService;

@Controller
@RequestMapping("/direcciones")
public class DireccionUsuarioController {
	
	@Autowired
    private DireccionUsuarioService direccionUsuarioService;

	// ==========================================
    // VISTA DE MIS DIRECCIONES
    // ==========================================

    @GetMapping("/vista")
    public String mostrarDirecciones(

            @RequestParam Integer idUsuario,

            Model model) {


        List<DireccionUsuario> direcciones =
                direccionUsuarioService
                .listarPorUsuario(
                        idUsuario
                );


        model.addAttribute(
                "direcciones",
                direcciones
        );


        return
            "fragmentos/mis-direcciones :: vistaDirecciones";
    }


    // ==========================================
    // ELIMINAR DIRECCIÓN
    // ==========================================

    @DeleteMapping(
        "/{idDireccionUsuario}"
    )
    @ResponseBody
    public ResponseEntity<Map<String, Object>>
    eliminarDireccion(

            @PathVariable
            Integer idDireccionUsuario,

            @RequestParam
            Integer idUsuario) {


        direccionUsuarioService
                .eliminarDireccion(
                        idDireccionUsuario,
                        idUsuario
                );


        Map<String, Object> respuesta =
                new HashMap<>();


        respuesta.put(
                "ok",
                true
        );


        respuesta.put(
                "mensaje",
                "Dirección eliminada correctamente"
        );


        return ResponseEntity.ok(
                respuesta
        );
    }
	
	
	
	
    @PostMapping(
        value = "/agregar",
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Map<String, Object>>
    agregarDireccion(

            @RequestParam Integer idUsuario,
            @RequestParam String alias,
            @RequestParam String direccion,

            @RequestParam(required = false)
            String referencia,

            @RequestParam BigDecimal latitud,
            @RequestParam BigDecimal longitud) {


        DireccionUsuario nueva =
                direccionUsuarioService
                .guardarDireccion(
                        idUsuario,
                        alias,
                        direccion,
                        referencia,
                        latitud,
                        longitud
                );


        Map<String, Object> respuesta =
                new HashMap<>();


        respuesta.put("ok", true);

        respuesta.put(
                "idDireccionUsuario",
                nueva.getIdDireccionUsuario()
        );

        respuesta.put(
                "mensaje",
                "Dirección guardada correctamente"
        );


        return ResponseEntity.ok(respuesta);
    }
    
}
