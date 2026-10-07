package com.proyecto.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.ZoneId;

@Entity
@Table(name="NEGOCIO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Negocio {
	
	public enum EstadoNegocio {ABIERTO,CERRADO}
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
	
	@Enumerated(EnumType.STRING)
	@Column(name = "estado", length = 10)
	private EstadoNegocio estado;
	
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
	
	@Column(name = "horaInicio")
	private LocalTime horaInicio;

	@Column(name = "horaFin")
	private LocalTime horaFin;
	
	@Column(name = "modoWhatsapp")
	private Boolean modoWhatsapp;
	
	@Column(name = "imagenCarta", length = 200)
	private String imagenCarta;
	
	public String obtenerEstadoReal() {
	    // Si el administrador configuró un horario, la hora del sistema toma el control
	    if (this.horaInicio != null && this.horaFin != null) {
	        LocalTime ahora = LocalTime.now(ZoneId.of("America/Lima"));
	        
	        if (horaInicio.isBefore(horaFin)) { 
	            // Horario diurno (Ej: 08:00 a 22:00)
	            if (!ahora.isBefore(horaInicio) && !ahora.isAfter(horaFin)) {
	                return "ABIERTO";
	            }
	        } else { 
	            // Horario nocturno (Ej: 18:00 a 02:00)
	            if (!ahora.isBefore(horaInicio) || !ahora.isAfter(horaFin)) {
	                return "ABIERTO";
	            }
	        }
	        return "CERRADO"; // Si tiene horario pero la hora actual no encaja
	    }
	    
	    // Si NO hay horario configurado (campos vacíos), respeta el botón manual de la base de datos
	    return (this.estado != null) ? this.estado.name() : "CERRADO";
	}
}