package com.proyecto.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.LocalDateTime;

@Entity
@Table(name="USUARIO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
	
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Integer idUsuario;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="idRol", nullable=false)
	@ToString.Exclude 
	private Rol rol;
	
	@Column(length=45)
	private String nombres;
	
	@Column(length=45)
	private String apellidos;
	
	@Column(length=8)
	private String dni;
	
	@Column(length=15)
	private String telefono;
	
	@Column(length=45, name="correoElectronico")
	private String correoElectronico;
	
	@Column(length=200)
	private String password;
	
	@Column(name="fechaCreacion", insertable=false, updatable=false)
	private LocalDateTime fechaCreacion;

}