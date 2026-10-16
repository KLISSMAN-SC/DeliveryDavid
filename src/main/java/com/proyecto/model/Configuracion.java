package com.proyecto.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CONFIGURACION")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Configuracion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "idConfiguracion")
  private Integer idConfiguracion;

  @Column(name = "telefonoSoporte", length = 20)
  private String telefonoSoporte;

  @Column(name = "linkFacebook", length = 255)
  private String linkFacebook;

  @Column(name = "linkInstagram", length = 255)
  private String linkInstagram;

  @Column(name = "linkTiktok", length = 255)
  private String linkTiktok;

  // Textos personalizados para WhatsApp
  @Column(name = "mensajeWspCabecera", length = 500)
  private String mensajeWspCabecera;

  @Column(name = "mensajeWspFooter", length = 500)
  private String mensajeWspFooter;

  // NUEVOS CAMPOS PARA TARIFAS DE DELIVERY
  @Column(name = "precioBaseDelivery", precision = 10, scale = 2)
  private java.math.BigDecimal precioBaseDelivery;

  @Column(name = "distanciaBaseMetros")
  private Integer distanciaBaseMetros;

  @Column(name = "precioKmAdicional", precision = 10, scale = 2)
  private java.math.BigDecimal precioKmAdicional;
}
