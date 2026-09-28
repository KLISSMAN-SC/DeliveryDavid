package com.proyecto.model;

import jakarta.persistence.Entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="autor")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Autor {
	
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable=false, length=100)
	private String nombre;
	
	@Column(nullable=false, length=100)
	private String apellido;
	
	@Column(length=100, name="fecha_nacimiento")
	private LocalDate fechaNacimiento;
	
	@Column(length=50)
	private String nacionalidad;
	
	@OneToMany(mappedBy="autor", cascade = CascadeType.ALL,orphanRemoval=true)
	private List<Libro> libros;

}
