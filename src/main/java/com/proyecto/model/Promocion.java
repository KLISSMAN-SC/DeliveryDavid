package com.proyecto.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "PROMOCION")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Promocion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPromocion;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(length = 255)
    private String descripcion;

    @Column(name = "imagenBanner", nullable = false, length = 255)
    private String imagenBanner;

    @Column(name = "fechaInicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fechaFin", nullable = false)
    private LocalDateTime fechaFin;

    @Column(columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean estado;

    @ManyToOne
    @JoinColumn(name = "idNegocio", nullable = false)
    private Negocio negocio;

    // El producto es Nullable en BD (para promociones generales del negocio)
    @ManyToOne
    @JoinColumn(name = "idProducto", nullable = true)
    private Producto producto;

  
}