package com.proyecto.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name="CENTRO_COMERCIAL")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CentroComercial {
	
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Integer idCentroComercial;
	
	@Column(nullable=false, length=45)
	private String nombre;
	
	@Column(length=45)
	private String direccion;
	
	@Column(precision=10, scale=8)
	private BigDecimal latitud;
	
	@Column(precision=10, scale=8)
	private BigDecimal longitud;

}