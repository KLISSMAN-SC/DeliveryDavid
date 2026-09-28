package com.proyecto.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name="libro")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Libro {

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable=false, length=150)
	private String titulo;
	
	
	@Column(nullable=false, length=20)
	private String isbn;
	
	
		@ManyToOne(fetch = FetchType.EAGER)
		@JoinColumn(name="autor_id" , nullable=false)
		@ToString.Exclude 
		private Autor autor;
		
		
		@ManyToOne(fetch = FetchType.EAGER)
		@JoinColumn(name="categoria_id" , nullable=false)
		@ToString.Exclude 
		private Categoria categoria;
	
	@Column(name="anio_publicacion")
	private Integer anioPublicacion;
	
	@Column(name="cantidad_paginas")
	private Integer cantidadPaginas;
	
}