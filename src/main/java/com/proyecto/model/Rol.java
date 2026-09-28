package com.proyecto.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="ROL")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rol {
	
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Integer idRol;
	
	@Column(length=45)
	private String nombre;

}