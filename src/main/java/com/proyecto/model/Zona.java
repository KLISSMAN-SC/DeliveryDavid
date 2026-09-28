package com.proyecto.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name="ZONA")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Zona {
	
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Integer idZona;
	
	@Column(length=45)
	private String nombre;
	
	@Column(name="costoEnvioBase", precision=10, scale=2)
	private BigDecimal costoEnvioBase;

}