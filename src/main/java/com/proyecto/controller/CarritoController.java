package com.proyecto.controller;

import com.proyecto.dto.CarritoView;
import com.proyecto.model.Usuario;
import com.proyecto.services.CarritoService;
import com.proyecto.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CarritoController {

  @Autowired private CarritoService carritoService;
  // HOLA
  @Autowired private UsuarioService usuarioService;

  @PostMapping("/carrito/agregar")
  public CarritoView agregarProducto(
      Authentication authentication,
      @RequestParam Integer idNegocio,
      @RequestParam Integer idProducto) {
    Usuario usuario = usuarioService.obtenerUsuarioAutenticado(authentication);
    return CarritoView.from(
        carritoService.agregarProducto(usuario.getIdUsuario(), idNegocio, idProducto));
  }

  @PostMapping("/carrito/disminuir")
  public CarritoView disminuirProducto(
      Authentication authentication,
      @RequestParam Integer idNegocio,
      @RequestParam Integer idProducto) {
    Usuario usuario = usuarioService.obtenerUsuarioAutenticado(authentication);
    return CarritoView.from(
        carritoService.disminuirProducto(usuario.getIdUsuario(), idNegocio, idProducto));
  }

  @DeleteMapping("/carrito/eliminar")
  public CarritoView eliminarProducto(
      Authentication authentication,
      @RequestParam Integer idNegocio,
      @RequestParam Integer idProducto) {
    Usuario usuario = usuarioService.obtenerUsuarioAutenticado(authentication);
    return CarritoView.from(
        carritoService.eliminarProducto(usuario.getIdUsuario(), idNegocio, idProducto));
  }

  // =====================================================
  // OBTENER CARRITO ACTUAL
  // =====================================================

  @GetMapping("/carrito/actual")
  public CarritoView obtenerCarritoActual(
      Authentication authentication, @RequestParam Integer idNegocio) {
    Usuario usuario = usuarioService.obtenerUsuarioAutenticado(authentication);
    return CarritoView.from(carritoService.obtenerCarritoActual(usuario.getIdUsuario(), idNegocio));
  }
}
