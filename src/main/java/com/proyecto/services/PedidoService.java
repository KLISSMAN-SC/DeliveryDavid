package com.proyecto.services;

import com.proyecto.model.*;
import com.proyecto.model.Pedido.EstadoPedido;
import com.proyecto.repository.*;
import com.proyecto.security.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PedidoService {
  private final PedidoRepository pedidos;
  private final CarritoRepository carts;
  private final DetallePedidoRepository details;
  private final DeliveryService delivery;
  private final DireccionUsuarioService addresses;
  private final SecurityAudit audit;

  public PedidoService(
      PedidoRepository pedidos,
      CarritoRepository carts,
      DetallePedidoRepository details,
      DeliveryService delivery,
      DireccionUsuarioService addresses,
      SecurityAudit audit) {
    this.pedidos = pedidos;
    this.carts = carts;
    this.details = details;
    this.delivery = delivery;
    this.addresses = addresses;
    this.audit = audit;
  }

  public Carrito obtenerCarritoCheckout(Integer u, Integer n) {
    return carts
        .findByUsuarioIdUsuarioAndNegocioIdNegocioAndEstado(u, n, "ACTIVO")
        .orElseThrow(BusinessException::missing);
  }

  public BigDecimal calcularSubtotal(Carrito c) {
    BigDecimal total = BigDecimal.ZERO;
    for (var d : c.getDetalles()) {
      InputChecks.money(d.getPrecioUnitario());
      if (d.getCantidad() == null || d.getCantidad() < 1 || d.getCantidad() > 99)
        throw BusinessException.invalid("Cantidad no válida.");
      total = total.add(d.getPrecioUnitario().multiply(BigDecimal.valueOf(d.getCantidad())));
    }
    InputChecks.money(total);
    return total;
  }

  @Transactional
  public Pedido confirmarPedido(
      Integer u,
      Integer n,
      Integer direccion,
      String metodo,
      BigDecimal propina,
      Integer idCarrito) {
    propina = propina == null ? BigDecimal.ZERO : propina;
    InputChecks.money(propina);
    if (propina.compareTo(new BigDecimal("1000")) > 0)
      throw BusinessException.invalid("La propina supera el límite permitido.");
    if (!Set.of("EFECTIVO", "YAPE", "PLIN").contains(metodo))
      throw BusinessException.invalid("Método de pago no válido.");
    Carrito c =
        (idCarrito == null
                ? carts.findActiveForUpdate(u, n)
                : carts.findCartForUpdate(idCarrito, u, n))
            .orElseThrow(BusinessException::missing);
    if (!"ACTIVO".equals(c.getEstado())) {
      return pedidos
          .findByIdCarritoOrigen(c.getIdCarrito())
          .filter(p -> p.getUsuario().getIdUsuario().equals(u))
          .orElseThrow(
              () -> new BusinessException(HttpStatus.CONFLICT, "Este carrito ya fue procesado."));
    }
    CarritoService.availableBusiness(c.getNegocio());
    if (c.getDetalles().isEmpty()) throw BusinessException.invalid("El carrito está vacío.");
    for (var d : c.getDetalles()) {
      if (!Boolean.TRUE.equals(d.getProducto().getDisponible())
          || !d.getProducto().getNegocio().getIdNegocio().equals(n))
        throw BusinessException.invalid("Hay productos no disponibles.");
      if (d.getProducto().getPrecio().compareTo(d.getPrecioUnitario()) != 0)
        throw new BusinessException(
            HttpStatus.CONFLICT, "El catálogo cambió. Actualiza el carrito antes de confirmar.");
    }
    var a = addresses.obtenerDireccionUsuario(direccion, u);
    var calculation = delivery.calcularDelivery(c.getNegocio(), a);
    InputChecks.money(calculation.costoEnvio());
    var total = calcularSubtotal(c).add(calculation.costoEnvio()).add(propina);
    InputChecks.money(total);
    var p = new Pedido();
    p.setIdCarritoOrigen(c.getIdCarrito());
    p.setUsuario(c.getUsuario());
    p.setNegocio(c.getNegocio());
    p.setEstadoPedido(EstadoPedido.PENDIENTE);
    p.setMetodoPago(metodo);
    p.setPropina(propina);
    p.setCostoEnvio(calculation.costoEnvio());
    p.setTotal(total);
    p.setDireccionEnvio(a.getDireccion());
    p.setReferencia(a.getReferencia());
    p.setLatitudEnvio(a.getLatitud());
    p.setLongitudEnvio(a.getLongitud());
    pedidos.saveAndFlush(p);
    for (var d : c.getDetalles()) {
      var item = new DetallePedido();
      item.setPedido(p);
      item.setProducto(d.getProducto());
      item.setCantidad(d.getCantidad());
      item.setPrecioUnitario(d.getPrecioUnitario());
      item.setSubtotal(d.getPrecioUnitario().multiply(BigDecimal.valueOf(d.getCantidad())));
      item.setNotaEspecial(d.getNotaEspecial());
      details.save(item);
    }
    c.setEstado("FINALIZADO");
    carts.saveAndFlush(c);
    audit.event("ORDER_CREATED", p.getIdPedido() + " user=" + u);
    return p;
  }

  @Transactional
  public Pedido cancelarPedido(Integer id, Integer u) {
    var p = pedidos.findForCancel(id, u).orElseThrow(BusinessException::missing);
    if (p.getEstadoPedido() == EstadoPedido.CANCELADO) return p;
    if (!Set.of(EstadoPedido.PENDIENTE, EstadoPedido.CONFIRMADO).contains(p.getEstadoPedido()))
      throw new BusinessException(HttpStatus.CONFLICT, "Este pedido ya no puede cancelarse.");
    p.setEstadoPedido(EstadoPedido.CANCELADO);
    pedidos.saveAndFlush(p);
    audit.event("ORDER_CANCELLED", id + " user=" + u);
    return p;
  }

  public Pedido obtenerPedidoUsuario(Integer id, Integer u) {
    return pedidos.findByIdPedidoAndUsuarioIdUsuario(id, u).orElseThrow(BusinessException::missing);
  }

  public List<Pedido> listarPedidosUsuario(Integer u) {
    return pedidos.findByUsuarioIdUsuarioOrderByIdPedidoDesc(u);
  }

  public List<DetallePedido> obtenerDetallesPedido(Integer id) {
    return details.findByPedidoIdPedido(id);
  }

  public Map<Integer, List<DetallePedido>> obtenerDetallesAgrupados(List<Pedido> orders) {
    if (orders.isEmpty()) return Map.of();
    return details
        .findByPedidoIdPedidoIn(orders.stream().map(Pedido::getIdPedido).toList())
        .stream()
        .collect(Collectors.groupingBy(d -> d.getPedido().getIdPedido()));
  }
}
