package com.proyecto.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proyecto.model.Carrito;
import com.proyecto.model.DireccionUsuario;
import com.proyecto.model.Pedido;
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


    @Value("${google.maps.api-key:}")
    private String googleMapsApiKey;


    // =======================================
    // ABRIR MODAL
    // =======================================

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


        // Direcciones disponibles
        List<DireccionUsuario> direcciones =
                direccionUsuarioService
                .listarPorUsuario(
                        idUsuario
                );


        // Principal
        DireccionUsuario
            direccionSeleccionada =
                direccionUsuarioService
                .obtenerPrincipalOPrimera(
                        idUsuario
                );

        //hola
        BigDecimal subtotal =
                pedidoService
                .calcularSubtotal(
                        carrito
                );


        BigDecimal envio =
                carrito
                .getNegocio()
                .getZona()
                .getCostoEnvioBase();


        if (envio == null) {
            envio = BigDecimal.ZERO;
        }


        BigDecimal total =
                subtotal.add(envio);


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


    // =======================================
    // CONFIRMAR
    // =======================================

    @PostMapping("/confirmar")
    @ResponseBody
    public Map<String, Object>
        confirmarPedido(

            @RequestParam Integer idUsuario,

            @RequestParam Integer idNegocio,

            @RequestParam Integer idDireccion,

            @RequestParam String metodoPago,

            @RequestParam(defaultValue = "0")
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


        return respuesta;
    }
	
	}

