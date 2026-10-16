package com.proyecto.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ZONA")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Zona {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer idZona;

  @Column(length = 45)
  private String nombre;

  @Column(name = "costoEnvioBase", precision = 10, scale = 2)
  private BigDecimal costoEnvioBase;
}
