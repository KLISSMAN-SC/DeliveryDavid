package com.proyecto.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.model.Carrito;
import com.proyecto.model.DetalleCarrito;
import com.proyecto.model.DetallePedido;
import com.proyecto.model.DireccionUsuario;
import com.proyecto.model.Pedido;
import com.proyecto.model.Pedido.EstadoPedido;
import com.proyecto.repository.CarritoRepository;
import com.proyecto.repository.DetallePedidoRepository;
import com.proyecto.repository.PedidoRepository;

import jakarta.transaction.Transactional;

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


	    // ========================================
	    // OBTENER CARRITO
	    // ========================================

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
	                        "No existe un carrito activo"
	                    )
	                );
	    }


	    // ========================================
	    // CALCULAR SUBTOTAL
	    // ========================================

	    public BigDecimal calcularSubtotal(
	            Carrito carrito) {

	        return carrito
	                .getDetalles()
	                .stream()

	                .map(detalle ->

	                    detalle
	                    .getPrecioUnitario()

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


	    // ========================================
	    // CONFIRMAR PEDIDO
	    // ========================================

	    @Transactional
	    public Pedido confirmarPedido(

	            Integer idUsuario,

	            Integer idNegocio,

	            Integer idDireccion,

	            String metodoPago,

	            BigDecimal propina) {


	        // 1. Obtener carrito
	        Carrito carrito =
	                obtenerCarritoCheckout(
	                        idUsuario,
	                        idNegocio
	                );


	        if (
	            carrito.getDetalles() == null ||
	            carrito.getDetalles().isEmpty()
	        ) {

	            throw new RuntimeException(
	                "El carrito está vacío"
	            );
	        }


	        // 2. Obtener dirección
	        // y verificar que pertenezca al usuario
	        DireccionUsuario direccion =
	                direccionUsuarioService
	                .obtenerDireccionUsuario(
	                        idDireccion,
	                        idUsuario
	                );


	        // 3. Calcular subtotal
	        BigDecimal subtotal =
	                calcularSubtotal(carrito);


	        // 4. Envío
	        BigDecimal costoEnvio =
	                carrito
	                .getNegocio()
	                .getZona()
	                .getCostoEnvioBase();


	        if (costoEnvio == null) {
	            costoEnvio = BigDecimal.ZERO;
	        }


	        // 5. Propina
	        if (propina == null) {
	            propina = BigDecimal.ZERO;
	        }


	        // 6. Total
	        BigDecimal total =
	                subtotal
	                .add(costoEnvio)
	                .add(propina);


	        // =====================================
	        // CREAR PEDIDO
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
	                EstadoPedido.PENDIENTE_DE_WHATSAPP
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


	        // MUY IMPORTANTE:
	        // hacemos una COPIA de la dirección

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


	        pedido =
	                pedidoRepository.save(
	                    pedido
	                );


	        // =====================================
	        // CARRITO → DETALLE PEDIDO
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
	        // FINALIZAR CARRITO
	        // =====================================

	        carrito.setEstado(
	                "FINALIZADO"
	        );


	        carritoRepository.save(
	                carrito
	        );


	        return pedido;
	    }
	
	 	
	}

