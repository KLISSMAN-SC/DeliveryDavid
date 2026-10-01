package com.proyecto.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CARRITO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Carrito {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCarrito;

    @Column(name="fechaCreacion", insertable=false, updatable=false)
    private LocalDateTime fechaCreacion;
    
    @Column(name="fechaActualizacion", insertable=false, updatable=false)
    private LocalDateTime fechaActualizacion;

    @Column(length = 20)
    private String estado; // ACTIVO / FINALIZADO / ABANDONADO
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idNegocio", nullable = false)
    private Negocio negocio;
    
    @OneToMany(
    	    mappedBy = "carrito",
    	    cascade = CascadeType.ALL,
    	    orphanRemoval = true
    	)
    	private List<DetalleCarrito> detalles = new ArrayList<>();
}