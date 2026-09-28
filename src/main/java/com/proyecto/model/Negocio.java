package com.proyecto.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.math.BigDecimal;

@Entity
@Table(name="NEGOCIO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Negocio {
	
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Integer idNegocio;
	
	@Column(length=45)
	private String nombre;
	
	@Column(length=50)
	private String direccion;
	
	@Column(precision=10, scale=8)
	private BigDecimal latitud;
	
	@Column(precision=10, scale=8)
	private BigDecimal longitud;
	
	@Column(length=15)
	private String telefono;
	
	@Column(length=10) // "ABIERTO" o "CERRADO"
	private String estado;
	
	@Column(name="imagenLogo", length=200)
	private String imagenLogo;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="idTipoNegocio", nullable=false)
	@ToString.Exclude 
	private TipoNegocio tipoNegocio;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="idZona", nullable=false)
	@ToString.Exclude 
	private Zona zona;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="idCentroComercial") // Puede ser nulo según tu BD
	@ToString.Exclude 
	private CentroComercial centroComercial;

}