package com.proyecto.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proyecto.dto.ResultadoDelivery;
import com.proyecto.model.Carrito;
import com.proyecto.model.DetallePedido;
import com.proyecto.model.DireccionUsuario;
import com.proyecto.model.Pedido;
import com.proyecto.services.DeliveryService;
import com.proyecto.services.DireccionUsuarioService;
import com.proyecto.services.PedidoService;



@Controller
@RequestMapping("/pedido")
public class PedidoController {
	@Autowired
    private PedidoService pedidoService;


    @Autowired
    private DireccionUsuarioService
            direccionUsuarioService;
    @Autowired
    private DeliveryService deliveryService;


    @Value("${google.maps.api-key:}")
    private String googleMapsApiKey;


    // =======================================
    // ABRIR MODAL
    // =======================================
    // ========================================
    // CHECKOUT
    // ========================================

    @GetMapping("/resumen")
    public String mostrarResumen(

            @RequestParam Integer idUsuario,
            @RequestParam Integer idNegocio,
            Model model) {


        Carrito carrito =
                pedidoService
                .obtenerCarritoCheckout(
                        idUsuario,
                        idNegocio
                );


        List<DireccionUsuario> direcciones =
                direccionUsuarioService
                .listarPorUsuario(
                        idUsuario
                );


        DireccionUsuario direccionSeleccionada =
                direccionUsuarioService
                .obtenerPrincipalOPrimera(
                        idUsuario
                );


        BigDecimal subtotal =
                pedidoService
                .calcularSubtotal(
                        carrito
                );

        BigDecimal envio =
                BigDecimal.ZERO;


        BigDecimal distanciaKm =
                BigDecimal.ZERO;

     // ==========================================
        // DELIVERY
        // ==========================================

        if (direccionSeleccionada != null) {

            ResultadoDelivery resultado =
                    deliveryService
                        .calcularDelivery(
                            carrito.getNegocio(),
                            direccionSeleccionada
                        );


            envio =
                    resultado.costoEnvio();


            distanciaKm =
                    resultado.distanciaKm();
        }


        BigDecimal total =
                subtotal.add(
                    envio
                );


        model.addAttribute(
                "carrito",
                carrito
        );

        model.addAttribute(
                "direcciones",
                direcciones
        );

        model.addAttribute(
                "direccionSeleccionada",
                direccionSeleccionada
        );

        model.addAttribute(
                "subtotal",
                subtotal
        );

        model.addAttribute(
                "envio",
                envio
        );
        model.addAttribute(
                "distanciaKm",
                distanciaKm
            );

        model.addAttribute(
                "total",
                total
        );

        model.addAttribute(
                "googleMapsKey",
                googleMapsApiKey
        );


        return
            "fragmentos/pedido-modal :: modalPedido";
    }


    // ========================================
    // CREAR PEDIDO
    // ========================================

    @PostMapping(
    		 value = "/confirmar",
    		  consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
    		  produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<Map<String, Object>>
    confirmarPedido(

            @RequestParam Integer idUsuario,
            @RequestParam Integer idNegocio,
            @RequestParam Integer idDireccion,
            @RequestParam String metodoPago,

            @RequestParam(
                defaultValue = "0"
            )
            BigDecimal propina) {


        Pedido pedido =
                pedidoService
                .confirmarPedido(
                        idUsuario,
                        idNegocio,
                        idDireccion,
                        metodoPago,
                        propina
                );


        Map<String, Object> respuesta =
                new HashMap<>();


        respuesta.put(
                "ok",
                true
        );

        respuesta.put(
                "idPedido",
                pedido.getIdPedido()
        );

        respuesta.put(
                "mensaje",
                "Pedido registrado correctamente"
        );


        return ResponseEntity
                .ok()
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .body(respuesta);
    }
    @PostMapping(
    	    value = "/{idPedido}/cancelar",
    	    produces = MediaType.APPLICATION_JSON_VALUE
    	)
    	@ResponseBody
    	public ResponseEntity<Map<String, Object>>
    	cancelarPedido(

    	        @PathVariable Integer idPedido,

    	        @RequestParam Integer idUsuario) {


    	    Pedido pedido =
    	            pedidoService
    	            .cancelarPedido(
    	                    idPedido,
    	                    idUsuario
    	            );


    	    Map<String, Object> respuesta =
    	            new HashMap<>();


    	    respuesta.put(
    	            "ok",
    	            true
    	    );


    	    respuesta.put(
    	            "idPedido",
    	            pedido.getIdPedido()
    	    );


    	    respuesta.put(
    	            "estado",
    	            pedido.getEstadoPedido()
    	    );


    	    respuesta.put(
    	            "mensaje",
    	            "Pedido cancelado correctamente"
    	    );


    	    return ResponseEntity
    	            .ok(respuesta);
    	}


    // ========================================
    // MODAL PEDIDO CREADO
    // ========================================

    @GetMapping(
        "/confirmacion/{idPedido}"
    )
    public String mostrarConfirmacion(

            @PathVariable Integer idPedido,

            Model model) {


        Pedido pedido =
                pedidoService
                .obtenerPedido(
                        idPedido
                );


        List<DetallePedido> detalles =
                pedidoService
                .obtenerDetallesPedido(
                        idPedido
                );


        model.addAttribute(
                "pedido",
                pedido
        );


        model.addAttribute(
                "detalles",
                detalles
        );


        model.addAttribute(
                "googleMapsKey",
                googleMapsApiKey
        );


        return
            "fragmentos/pedido-confirmado-modal :: modalPedidoConfirmado";
    }
    @GetMapping("/mis-pedidos")
    public String mostrarMisPedidos(

            @RequestParam Integer idUsuario,

            Model model) {


        List<Pedido> pedidos =
                pedidoService
                .listarPedidosUsuario(
                        idUsuario
                );


        model.addAttribute(
                "pedidos",
                pedidos
        );


        model.addAttribute(
                "idUsuarioValidado",
                idUsuario
        );


        return "mis-pedidos";
    }
    @GetMapping(
    	    value = "/calcular-envio",
    	    produces = MediaType.APPLICATION_JSON_VALUE
    	)
    	@ResponseBody
    	public ResponseEntity<Map<String, Object>>
    	calcularEnvio(

    	        @RequestParam Integer idUsuario,

    	        @RequestParam Integer idNegocio,

    	        @RequestParam Integer idDireccionUsuario) {


    	    try {


    	        Carrito carrito =
    	                pedidoService
    	                    .obtenerCarritoCheckout(
    	                        idUsuario,
    	                        idNegocio
    	                    );


    	        DireccionUsuario direccion =
    	                direccionUsuarioService
    	                    .obtenerPorIdYUsuario(
    	                        idDireccionUsuario,
    	                        idUsuario
    	                    );


    	        BigDecimal subtotal =
    	                pedidoService
    	                    .calcularSubtotal(
    	                        carrito
    	                    );


    	        ResultadoDelivery resultado =
    	                deliveryService
    	                    .calcularDelivery(
    	                        carrito.getNegocio(),
    	                        direccion
    	                    );


    	        BigDecimal total =
    	                subtotal.add(
    	                    resultado.costoEnvio()
    	                );


    	        Map<String, Object> respuesta =
    	                new HashMap<>();


    	        respuesta.put(
    	            "ok",
    	            true
    	        );


    	        respuesta.put(
    	            "distanciaKm",
    	            resultado.distanciaKm()
    	        );


    	        respuesta.put(
    	            "costoEnvio",
    	            resultado.costoEnvio()
    	        );


    	        respuesta.put(
    	            "subtotal",
    	            subtotal
    	        );


    	        respuesta.put(
    	            "total",
    	            total
    	        );


    	        return ResponseEntity.ok(
    	            respuesta
    	        );


    	    } catch (Exception e) {


    	        return ResponseEntity
    	                .badRequest()
    	                .body(
    	                    Map.of(
    	                        "ok",
    	                        false,
    	                        "mensaje",
    	                        e.getMessage()
    	                    )
    	                );
    	    }
    	}
   
	
	}

