package com.proyecto.dto;

import com.proyecto.model.Carrito;
import java.math.BigDecimal;
import java.util.List;

public record CarritoView(Integer idCarrito, NegocioView negocio, List<DetalleView> detalles) {
  public record NegocioView(Integer idNegocio) {}

  public record ProductoView(Integer idProducto, String nombre) {}

  public record DetalleView(Integer cantidad, BigDecimal precioUnitario, ProductoView producto) {}

  public static CarritoView from(Carrito c) {
    if (c == null) return null;
    return new CarritoView(
        c.getIdCarrito(),
        new NegocioView(c.getNegocio().getIdNegocio()),
        c.getDetalles().stream()
            .map(
                d ->
                    new DetalleView(
                        d.getCantidad(),
                        d.getPrecioUnitario(),
                        new ProductoView(
                            d.getProducto().getIdProducto(), d.getProducto().getNombre())))
            .toList());
  }
}
