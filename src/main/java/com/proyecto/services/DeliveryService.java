package com.proyecto.services;

import com.proyecto.dto.ResultadoDelivery;
import com.proyecto.model.Configuracion;
import com.proyecto.model.DireccionUsuario;
import com.proyecto.model.Negocio;
import com.proyecto.repository.ConfiguracionRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeliveryService {

  @Autowired private ConfiguracionRepository configuracionRepository;

  // Radio medio de la Tierra
  private static final double RADIO_TIERRA_KM = 6371.0088;

  public ResultadoDelivery calcularDelivery(Negocio negocio, DireccionUsuario direccion) {

    // ==========================================
    // VALIDAR COORDENADAS
    // ==========================================

    if (negocio == null || negocio.getLatitud() == null || negocio.getLongitud() == null) {

      throw new IllegalStateException("El negocio no tiene coordenadas configuradas.");
    }

    if (direccion == null || direccion.getLatitud() == null || direccion.getLongitud() == null) {

      throw new IllegalStateException("La dirección de entrega no tiene coordenadas.");
    }

    // ==========================================
    // CONFIGURACIÓN GLOBAL
    // ==========================================

    Configuracion config =
        configuracionRepository
            .findFirstByOrderByIdConfiguracionAsc()
            .orElseThrow(() -> new IllegalStateException("No existe configuración global."));

    if (config.getPrecioBaseDelivery() == null) {

      throw new IllegalStateException("El precio base del delivery no está configurado.");
    }

    if (config.getDistanciaBaseMetros() == null) {

      throw new IllegalStateException("La distancia base del delivery no está configurada.");
    }

    if (config.getPrecioKmAdicional() == null) {

      throw new IllegalStateException("El precio por kilómetro adicional no está configurado.");
    }

    // ==========================================
    // DISTANCIA HAVERSINE
    // ==========================================

    double distanciaKmDouble =
        calcularDistanciaKm(
            negocio.getLatitud().doubleValue(),
            negocio.getLongitud().doubleValue(),
            direccion.getLatitud().doubleValue(),
            direccion.getLongitud().doubleValue());

    BigDecimal distanciaKm = BigDecimal.valueOf(distanciaKmDouble);

    // ==========================================
    // CONFIGURACIÓN MONETARIA
    // ==========================================

    com.proyecto.security.InputChecks.coordinates(negocio.getLatitud(), negocio.getLongitud());
    com.proyecto.security.InputChecks.coordinates(direccion.getLatitud(), direccion.getLongitud());
    com.proyecto.security.InputChecks.money(config.getPrecioBaseDelivery());
    com.proyecto.security.InputChecks.money(config.getPrecioKmAdicional());
    if (config.getDistanciaBaseMetros() < 0)
      throw new IllegalStateException("Configuración de delivery no válida");
    BigDecimal precioBase = config.getPrecioBaseDelivery();

    BigDecimal precioKmAdicional = config.getPrecioKmAdicional();

    BigDecimal radioBaseKm =
        BigDecimal.valueOf(config.getDistanciaBaseMetros())
            .divide(BigDecimal.valueOf(1000), 6, RoundingMode.HALF_UP);

    // ==========================================
    // CALCULAR COSTO
    // ==========================================

    BigDecimal costoEnvio = precioBase;

    if (distanciaKm.compareTo(radioBaseKm) > 0) {

      // ==========================================
      // DISTANCIA QUE SUPERA EL RADIO BASE
      // ==========================================

      BigDecimal kmAdicionales = distanciaKm.subtract(radioBaseKm);

      // ==========================================
      // REDONDEO MATEMÁTICO
      //
      // 1.49 -> 1
      // 1.50 -> 2
      // 2.35 -> 2
      // 2.50 -> 3
      // ==========================================

      BigDecimal kmAdicionalesCobrables = kmAdicionales.setScale(0, RoundingMode.HALF_UP);

      // ==========================================
      // COSTO ADICIONAL
      // ==========================================

      BigDecimal costoAdicional = kmAdicionalesCobrables.multiply(precioKmAdicional);

      // ==========================================
      // COSTO TOTAL DE DELIVERY
      // ==========================================

      costoEnvio = precioBase.add(costoAdicional);
    }

    // ==========================================
    // REDONDEOS
    // ==========================================

    distanciaKm = distanciaKm.setScale(2, RoundingMode.HALF_UP);

    costoEnvio = costoEnvio.setScale(2, RoundingMode.HALF_UP);

    return new ResultadoDelivery(distanciaKm, costoEnvio);
  }

  // ==========================================
  // HAVERSINE
  // ==========================================

  private double calcularDistanciaKm(double lat1, double lon1, double lat2, double lon2) {

    double lat1Rad = Math.toRadians(lat1);

    double lat2Rad = Math.toRadians(lat2);

    double deltaLat = Math.toRadians(lat2 - lat1);

    double deltaLon = Math.toRadians(lon2 - lon1);

    double a =
        Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
            + Math.cos(lat1Rad)
                * Math.cos(lat2Rad)
                * Math.sin(deltaLon / 2)
                * Math.sin(deltaLon / 2);

    double c =
        2
            * Math.atan2(
                Math.sqrt(Math.max(0, Math.min(1, a))), Math.sqrt(1 - Math.max(0, Math.min(1, a))));

    return RADIO_TIERRA_KM * c;
  }
}
