package com.proyecto.controller;

import com.proyecto.model.DireccionUsuario;
import com.proyecto.model.Usuario;
import com.proyecto.services.DireccionUsuarioService;
import com.proyecto.services.UsuarioService;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/direcciones")
public class DireccionUsuarioController {

  @Autowired private DireccionUsuarioService direccionUsuarioService;
  @Autowired private UsuarioService usuarioService;

  // ==========================================
  // MODULO DE MIS DIRECCIONES
  // ==========================================

  @Value("${google.maps.api-key:}")
  private String googleMapsApiKey;

  @GetMapping
  public String paginaDirecciones(Authentication authentication, Model model) {

    Usuario usuario = usuarioService.obtenerUsuarioAutenticado(authentication);

    List<DireccionUsuario> direcciones =
        direccionUsuarioService.listarPorUsuario(usuario.getIdUsuario());

    model.addAttribute("direcciones", direcciones);

    model.addAttribute("googleMapsKey", googleMapsApiKey);

    return "mis-direcciones";
  }

  // ==========================================
  // ELIMINAR DIRECCIÓN
  // ==========================================

  @DeleteMapping("/{idDireccionUsuario}")
  @ResponseBody
  public ResponseEntity<Map<String, Object>> eliminarDireccion(
      @PathVariable Integer idDireccionUsuario, Authentication authentication) {
    Usuario usuario = usuarioService.obtenerUsuarioAutenticado(authentication);

    direccionUsuarioService.eliminarDireccion(idDireccionUsuario, usuario.getIdUsuario());

    Map<String, Object> respuesta = new HashMap<>();

    respuesta.put("ok", true);

    respuesta.put("mensaje", "Dirección eliminada correctamente");

    return ResponseEntity.ok(respuesta);
  }

  @PostMapping(
      value = "/agregar",
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Map<String, Object>> agregarDireccion(
      Authentication authentication,
      @RequestParam String alias,
      @RequestParam String direccion,
      @RequestParam(required = false) String referencia,
      @RequestParam BigDecimal latitud,
      @RequestParam BigDecimal longitud) {

    Usuario usuario = usuarioService.obtenerUsuarioAutenticado(authentication);
    DireccionUsuario nueva =
        direccionUsuarioService.guardarDireccion(
            usuario.getIdUsuario(), alias, direccion, referencia, latitud, longitud);

    Map<String, Object> respuesta = new HashMap<>();

    respuesta.put("ok", true);

    respuesta.put("idDireccionUsuario", nueva.getIdDireccionUsuario());

    respuesta.put("mensaje", "Dirección guardada correctamente");

    return ResponseEntity.ok(respuesta);
  }
}
