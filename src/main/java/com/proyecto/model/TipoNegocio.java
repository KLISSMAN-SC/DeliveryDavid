package com.proyecto.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="TIPO_NEGOCIO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoNegocio {
	
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Integer idTipoNegocio;
	
	@Column(length=45)
	private String nombre;
	
	@Column(length=200)
	private String icono;

}