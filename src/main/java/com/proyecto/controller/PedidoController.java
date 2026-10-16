package com.proyecto.controller;

import com.proyecto.model.*;
import com.proyecto.security.*;
import com.proyecto.services.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/pedido")
public class PedidoController {
  private final PedidoService pedidos;
  private final UsuarioService users;
  private final DireccionUsuarioService addresses;
  private final DeliveryService delivery;
  private final RateLimits limits;

  @Value("${google.maps.api-key:}")
  private String mapsKey;

  public PedidoController(
      PedidoService pedidos,
      UsuarioService users,
      DireccionUsuarioService addresses,
      DeliveryService delivery,
      RateLimits limits) {
    this.pedidos = pedidos;
    this.users = users;
    this.addresses = addresses;
    this.delivery = delivery;
    this.limits = limits;
  }

  @GetMapping("/resumen")
  public String mostrarResumen(@RequestParam Integer idNegocio, Authentication auth, Model m) {
    var u = users.obtenerUsuarioAutenticado(auth);
    var c = pedidos.obtenerCarritoCheckout(u.getIdUsuario(), idNegocio);
    var a = addresses.obtenerPrincipalOPrimera(u.getIdUsuario());
    var subtotal = pedidos.calcularSubtotal(c);
    var env = BigDecimal.ZERO;
    var km = BigDecimal.ZERO;
    if (a != null) {
      var r = delivery.calcularDelivery(c.getNegocio(), a);
      env = r.costoEnvio();
      km = r.distanciaKm();
    }
    m.addAttribute("carrito", c);
    m.addAttribute("direcciones", addresses.listarPorUsuario(u.getIdUsuario()));
    m.addAttribute("direccionSeleccionada", a);
    m.addAttribute("subtotal", subtotal);
    m.addAttribute("envio", env);
    m.addAttribute("distanciaKm", km);
    m.addAttribute("total", subtotal.add(env));
    m.addAttribute("googleMapsKey", mapsKey);
    return "fragmentos/pedido-modal :: modalPedido";
  }

  @PostMapping(
      value = "/confirmar",
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public Map<String, Object> confirmarPedido(
      Authentication auth,
      @RequestParam Integer idNegocio,
      @RequestParam Integer idDireccion,
      @RequestParam String metodoPago,
      @RequestParam(defaultValue = "0") BigDecimal propina,
      @RequestParam(required = false) Integer idCarrito) {
    var u = users.obtenerUsuarioAutenticado(auth);
    limits.check("order-account", u.getIdUsuario().toString(), 20, Duration.ofMinutes(1));
    var p =
        pedidos.confirmarPedido(
            u.getIdUsuario(), idNegocio, idDireccion, metodoPago, propina, idCarrito);
    return Map.of(
        "ok", true, "idPedido", p.getIdPedido(), "mensaje", "Pedido registrado correctamente");
  }

  @PostMapping(value = "/{idPedido}/cancelar", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public Map<String, Object> cancelarPedido(@PathVariable Integer idPedido, Authentication auth) {
    var p = pedidos.cancelarPedido(idPedido, users.obtenerUsuarioAutenticado(auth).getIdUsuario());
    return Map.of(
        "ok",
        true,
        "idPedido",
        p.getIdPedido(),
        "estado",
        p.getEstadoPedido(),
        "mensaje",
        "Pedido cancelado correctamente");
  }

  @GetMapping("/confirmacion/{idPedido}")
  public String mostrarConfirmacion(@PathVariable Integer idPedido, Authentication auth, Model m) {
    var p =
        pedidos.obtenerPedidoUsuario(
            idPedido, users.obtenerUsuarioAutenticado(auth).getIdUsuario());
    m.addAttribute("pedido", p);
    m.addAttribute("detalles", pedidos.obtenerDetallesPedido(p.getIdPedido()));
    m.addAttribute("googleMapsKey", mapsKey);
    return "fragmentos/pedido-confirmado-modal :: modalPedidoConfirmado";
  }

  @GetMapping("/mis-pedidos")
  public String mostrarMisPedidos(Authentication auth, Model m) {
    m.addAttribute(
        "pedidos",
        pedidos.listarPedidosUsuario(users.obtenerUsuarioAutenticado(auth).getIdUsuario()));
    return "mis-pedidos";
  }

  @GetMapping(value = "/calcular-envio", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public Map<String, Object> calcularEnvio(
      Authentication auth,
      @RequestParam Integer idNegocio,
      @RequestParam Integer idDireccionUsuario) {
    var u = users.obtenerUsuarioAutenticado(auth);
    var c = pedidos.obtenerCarritoCheckout(u.getIdUsuario(), idNegocio);
    var a = addresses.obtenerPorIdYUsuario(idDireccionUsuario, u.getIdUsuario());
    var r = delivery.calcularDelivery(c.getNegocio(), a);
    var subtotal = pedidos.calcularSubtotal(c);
    return Map.of(
        "ok",
        true,
        "distanciaKm",
        r.distanciaKm(),
        "costoEnvio",
        r.costoEnvio(),
        "subtotal",
        subtotal,
        "total",
        subtotal.add(r.costoEnvio()));
  }
}
