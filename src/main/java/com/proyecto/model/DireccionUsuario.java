package com.proyecto.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name="Direccion_Usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DireccionUsuario {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDireccionUsuario;

    @Column(length = 30,nullable = false)
    private String alias;

    @Column(length = 150,nullable = false)
    private String direccion;

    @Column(length = 150)
    private String referencia;

    @Column(precision = 10,scale = 8,nullable = false)
    private BigDecimal latitud;

    @Column(precision = 10,scale =8,nullable = false)
    private BigDecimal longitud;

    @Column(nullable = false)
    private Boolean principal = false;

    @Column(name = "fechaCreacion",insertable = false,updatable = false)
    private LocalDateTime fechaCreacion;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idUsuario",nullable = false)
    @ToString.Exclude
    private Usuario usuario;
}

