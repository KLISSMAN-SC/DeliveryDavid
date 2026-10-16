package com.proyecto.services;

import com.proyecto.model.*;
import com.proyecto.repository.*;
import com.proyecto.security.*;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CarritoService {
  private final CarritoRepository carts;
  private final ProductoRepository products;
  private final NegocioRepository businesses;
  private final UsuarioRepository users;

  public CarritoService(
      CarritoRepository carts,
      ProductoRepository products,
      NegocioRepository businesses,
      UsuarioRepository users) {
    this.carts = carts;
    this.products = products;
    this.businesses = businesses;
    this.users = users;
  }

  public static void availableBusiness(Negocio n) {
    if (!"ABIERTO".equals(n.obtenerEstadoReal())
        || n.getTipoNegocio() == null
        || !Boolean.TRUE.equals(n.getTipoNegocio().getEstado()))
      throw BusinessException.invalid("El negocio no está disponible para recibir pedidos.");
  }

  @Transactional
  public Carrito agregarProducto(Integer user, Integer business, Integer product) {
    var n = businesses.findById(business).orElseThrow(BusinessException::missing);
    availableBusiness(n);
    var p = products.findById(product).orElseThrow(BusinessException::missing);
    if (!p.getNegocio().getIdNegocio().equals(business) || !Boolean.TRUE.equals(p.getDisponible()))
      throw BusinessException.invalid("El producto no está disponible en este negocio.");
    InputChecks.money(p.getPrecio());
    var c =
        carts
            .findActiveForUpdate(user, business)
            .orElseGet(
                () -> {
                  var fresh = new Carrito();
                  fresh.setUsuario(users.getReferenceById(user));
                  fresh.setNegocio(n);
                  fresh.setEstado("ACTIVO");
                  fresh.setFechaCreacion(LocalDateTime.now());
                  fresh.setFechaActualizacion(LocalDateTime.now());
                  return carts.saveAndFlush(fresh);
                });
    var detail =
        c.getDetalles().stream()
            .filter(d -> d.getProducto().getIdProducto().equals(product))
            .findFirst()
            .orElse(null);
    if (detail != null) {
      if (detail.getCantidad() >= 99)
        throw BusinessException.invalid("Máximo 99 unidades por producto.");
      detail.setCantidad(detail.getCantidad() + 1);
      detail.setPrecioUnitario(p.getPrecio());
    } else {
      if (c.getDetalles().size() >= 100)
        throw BusinessException.invalid("El carrito alcanzó el límite de productos.");
      detail = new DetalleCarrito();
      detail.setCarrito(c);
      detail.setProducto(p);
      detail.setCantidad(1);
      detail.setPrecioUnitario(p.getPrecio());
      c.getDetalles().add(detail);
    }
    return carts.saveAndFlush(c);
  }

  private Carrito locked(Integer u, Integer b) {
    return carts.findActiveForUpdate(u, b).orElseThrow(BusinessException::missing);
  }

  @Transactional
  public Carrito disminuirProducto(Integer u, Integer b, Integer p) {
    var c = locked(u, b);
    var d =
        c.getDetalles().stream()
            .filter(x -> x.getProducto().getIdProducto().equals(p))
            .findFirst()
            .orElseThrow(BusinessException::missing);
    if (d.getCantidad() > 1) d.setCantidad(d.getCantidad() - 1);
    else c.getDetalles().remove(d);
    return carts.saveAndFlush(c);
  }

  @Transactional
  public Carrito eliminarProducto(Integer u, Integer b, Integer p) {
    var c = locked(u, b);
    if (!c.getDetalles().removeIf(x -> x.getProducto().getIdProducto().equals(p)))
      throw BusinessException.missing();
    return carts.saveAndFlush(c);
  }

  @Transactional(readOnly = true)
  public Carrito obtenerCarritoActual(Integer u, Integer b) {
    return carts.findByUsuarioIdUsuarioAndNegocioIdNegocioAndEstado(u, b, "ACTIVO").orElse(null);
  }
}
