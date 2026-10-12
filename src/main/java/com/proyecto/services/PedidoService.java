package com.proyecto.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.model.Carrito;
import com.proyecto.model.DetalleCarrito;
import com.proyecto.model.DetallePedido;
import com.proyecto.model.DireccionUsuario;
import com.proyecto.model.Pedido;
import com.proyecto.model.Pedido.EstadoPedido;
import com.proyecto.repository.CarritoRepository;
import com.proyecto.repository.DetallePedidoRepository;
import com.proyecto.repository.PedidoRepository;



@Service
public class PedidoService {
	 
		@Autowired
	    private PedidoRepository pedidoRepository;


	    @Autowired
	    private DetallePedidoRepository
	            detallePedidoRepository;


	    @Autowired
	    private CarritoRepository
	            carritoRepository;


	    @Autowired
	    private DireccionUsuarioService
	            direccionUsuarioService;


	    public Carrito obtenerCarritoCheckout(
	            Integer idUsuario,
	            Integer idNegocio) {

	        return carritoRepository
	                .findByUsuarioIdUsuarioAndNegocioIdNegocioAndEstado(
	                        idUsuario,
	                        idNegocio,
	                        "ACTIVO"
	                )
	                .orElseThrow(() ->
	                    new RuntimeException(
	                        "No existe carrito activo"
	                    )
	                );
	    }


	    // ==========================================
	    // SUBTOTAL
	    // ==========================================

	    public BigDecimal calcularSubtotal(
	            Carrito carrito) {

	        return carrito
	                .getDetalles()
	                .stream()

	                .map(detalle ->

	                    detalle.getPrecioUnitario()
	                    .multiply(
	                        BigDecimal.valueOf(
	                            detalle.getCantidad()
	                        )
	                    )

	                )

	                .reduce(
	                    BigDecimal.ZERO,
	                    BigDecimal::add
	                );
	    }


	    // ==========================================
	    // CONFIRMAR PEDIDO
	    // ==========================================

	    @Transactional
	    public Pedido confirmarPedido(

	            Integer idUsuario,
	            Integer idNegocio,
	            Integer idDireccionUsuario,
	            String metodoPago,
	            BigDecimal propina) {


	        // =====================================
	        // 1. OBTENER CARRITO
	        // =====================================

	        Carrito carrito =
	                obtenerCarritoCheckout(
	                        idUsuario,
	                        idNegocio
	                );


	        if (carrito.getDetalles() == null ||
	            carrito.getDetalles().isEmpty()) {

	            throw new RuntimeException(
	                    "El carrito está vacío"
	            );
	        }


	        // =====================================
	        // 2. DIRECCIÓN
	        // =====================================

	        DireccionUsuario direccion =
	                direccionUsuarioService
	                .obtenerDireccionUsuario(
	                        idDireccionUsuario,
	                        idUsuario
	                );


	        // =====================================
	        // 3. TOTALES
	        // =====================================

	        BigDecimal subtotal =
	                calcularSubtotal(carrito);


	        BigDecimal costoEnvio =
	                carrito
	                .getNegocio()
	                .getZona()
	                .getCostoEnvioBase();


	        if (costoEnvio == null) {
	            costoEnvio = BigDecimal.ZERO;
	        }


	        if (propina == null) {
	            propina = BigDecimal.ZERO;
	        }


	        BigDecimal total =
	                subtotal
	                .add(costoEnvio)
	                .add(propina);


	        // =====================================
	        // 4. INSERT PEDIDO
	        // =====================================

	        Pedido pedido =
	                new Pedido();


	        pedido.setUsuario(
	                carrito.getUsuario()
	        );

	        pedido.setNegocio(
	                carrito.getNegocio()
	        );


	        pedido.setEstadoPedido(
	                EstadoPedido.PENDIENTE
	        );


	        pedido.setMetodoPago(
	                metodoPago
	        );


	        pedido.setCostoEnvio(
	                costoEnvio
	        );


	        pedido.setPropina(
	                propina
	        );


	        pedido.setTotal(
	                total
	        );


	        // Copia histórica de dirección

	        pedido.setDireccionEnvio(
	                direccion.getDireccion()
	        );

	        pedido.setReferencia(
	                direccion.getReferencia()
	        );

	        pedido.setLatitudEnvio(
	                direccion.getLatitud()
	        );

	        pedido.setLongitudEnvio(
	                direccion.getLongitud()
	        );


	        // Si tu fecha la maneja MySQL automáticamente,
	        // NO pongas esto.
	        //
	        // pedido.setFechaHora(
	        //        LocalDateTime.now()
	        // );


	        pedido =
	            pedidoRepository.save(pedido);


	        // =====================================
	        // 5. DETALLE_CARRITO → DETALLE_PEDIDO
	        // =====================================

	        for (
	            DetalleCarrito detalleCarrito
	                : carrito.getDetalles()
	        ) {

	            DetallePedido detallePedido =
	                    new DetallePedido();


	            detallePedido.setPedido(
	                    pedido
	            );


	            detallePedido.setProducto(
	                    detalleCarrito.getProducto()
	            );


	            detallePedido.setCantidad(
	                    detalleCarrito.getCantidad()
	            );


	            detallePedido.setPrecioUnitario(
	                    detalleCarrito
	                    .getPrecioUnitario()
	            );


	            BigDecimal subtotalDetalle =
	                    detalleCarrito
	                    .getPrecioUnitario()
	                    .multiply(
	                        BigDecimal.valueOf(
	                            detalleCarrito
	                            .getCantidad()
	                        )
	                    );


	            detallePedido.setSubtotal(
	                    subtotalDetalle
	            );


	            detallePedido.setNotaEspecial(
	                    detalleCarrito
	                    .getNotaEspecial()
	            );


	            detallePedidoRepository.save(
	                    detallePedido
	            );
	        }


	        // =====================================
	        // 6. FINALIZAR CARRITO
	        // =====================================

	        carrito.setEstado(
	                "FINALIZADO"
	        );


	        carritoRepository.save(
	                carrito
	        );


	        return pedido;
	    }
	    @Transactional
	    public Pedido cancelarPedido(
	            Integer idPedido,
	            Integer idUsuario) {


	        Pedido pedido =
	                pedidoRepository
	                .findByIdPedidoAndUsuarioIdUsuario(
	                        idPedido,
	                        idUsuario
	                )
	                .orElseThrow(() ->
	                    new RuntimeException(
	                        "Pedido no encontrado"
	                    )
	                );


	        String estadoActual =
	                pedido.getEstadoPedido().toString();


	        // Ya cancelado
	        if ("CANCELADO".equals(estadoActual)) {

	            throw new RuntimeException(
	                "El pedido ya se encuentra cancelado"
	            );
	        }


	        // Ya entregado
	        if ("ENTREGADO".equals(estadoActual)) {

	            throw new RuntimeException(
	                "Un pedido entregado no puede cancelarse"
	            );
	        }


	        // Si ya salió a reparto, tampoco
	        if ("EN_CAMINO".equals(estadoActual)) {

	            throw new RuntimeException(
	                "El pedido ya está en camino y no puede cancelarse"
	            );
	        }


	        pedido.setEstadoPedido(
	                EstadoPedido.CANCELADO
	        );


	        return pedidoRepository.save(
	                pedido
	        );

	    }
	    // ==========================================
	    // OBTENER PEDIDO
	    // ==========================================

	    public Pedido obtenerPedido(
	            Integer idPedido) {

	        return pedidoRepository
	                .findById(idPedido)
	                .orElseThrow(() ->
	                    new RuntimeException(
	                        "Pedido no encontrado"
	                    )
	                );
	    }
	    public List<Pedido> listarPedidosUsuario(
	            Integer idUsuario) {

	        return pedidoRepository
	                .findByUsuarioIdUsuarioOrderByIdPedidoDesc(
	                        idUsuario
	                );
	    }


	    // ==========================================
	    // OBTENER DETALLES
	    // ==========================================

	    public List<DetallePedido>
	    obtenerDetallesPedido(
	            Integer idPedido) {

	        return detallePedidoRepository
	                .findByPedidoIdPedido(
	                        idPedido
	                );
	    }
	    public Map<Integer, List<DetallePedido>>
	    obtenerDetallesAgrupados(
	            List<Pedido> pedidos) {


	        if (pedidos.isEmpty()) {

	            return new HashMap<>();
	        }


	        List<Integer> idsPedidos =
	                pedidos.stream()
	                .map(Pedido::getIdPedido)
	                .toList();


	        List<DetallePedido> detalles =
	                detallePedidoRepository
	                .findByPedidoIdPedidoIn(
	                        idsPedidos
	                );


	        return detalles.stream()
	                .collect(
	                    Collectors.groupingBy(
	                        detalle ->
	                            detalle
	                            .getPedido()
	                            .getIdPedido()
	                    )
	                );
	    }
	
	 	
	}

