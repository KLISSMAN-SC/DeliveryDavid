package com.proyecto.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "DETALLE_CARRITO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleCarrito {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDetalleCarrito;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal precioUnitario;
    
    @Column(name="notaEspecial", length=100)
	private String notaEspecial;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idCarrito", nullable = false)
    @JsonIgnore
    private Carrito carrito;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idProducto", nullable = false)
    private Producto producto;

    
}