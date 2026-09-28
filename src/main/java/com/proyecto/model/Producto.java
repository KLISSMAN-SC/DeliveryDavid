package com.proyecto.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.math.BigDecimal;

@Entity
@Table(name="PRODUCTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {
	
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Integer idProducto;
	
	@Column(length=45)
	private String nombre;
	
	@Column(length=255)
	private String descripcion;
	
	@Column(precision=10, scale=2)
	private BigDecimal precio;
	
	@Column(length=255)
	private String imagen;
	
	private Boolean disponible;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="idNegocio", nullable=false)
	@ToString.Exclude 
	private Negocio negocio;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="idCategoriaProducto", nullable=false)
	@ToString.Exclude 
	private CategoriaProducto categoriaProducto;

}