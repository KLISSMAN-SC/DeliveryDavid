package com.proyecto.model;

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
@Table(name = "CONSENTIMIENTO_LEGAL")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsentimientoLegal {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idConsentimiento;

    @Column(name = "versionTerminos",length = 30,nullable = false)
    private String versionTerminos;

    @Column(name = "versionPrivacidad", length = 30,nullable = false)
    private String versionPrivacidad;
    
    @Column(name = "fechaAceptacion",nullable = false)
    private LocalDateTime fechaAceptacion;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idUsuario",nullable = false)
    @ToString.Exclude
    private Usuario usuario;
}
