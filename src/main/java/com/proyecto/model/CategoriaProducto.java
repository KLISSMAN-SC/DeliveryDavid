package com.proyecto.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name="CATEGORIAS_PRODUCTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaProducto {
	
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Integer idCategoriaProducto;
	
	@Column(length=45)
	private String nombre;
	
	@Column(length=45)
	private String logo	;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="idNegocio", nullable=false)
	@ToString.Exclude 
	private Negocio negocio;

}