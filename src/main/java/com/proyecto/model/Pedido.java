package com.proyecto.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="PEDIDO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {
	
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Integer idPedido;
	
	@Column(name="estado_pedido", length=45)
	private String estadoPedido;
	
	@Column(name="metodo_pago", length=45)
	private String metodoPago;
	
	@Column(name="costo_envio", precision=10, scale=2)
	private BigDecimal costoEnvio;
	
	@Column(precision=10, scale=2)
	private BigDecimal propina;
	
	@Column(precision=10, scale=2)
	private BigDecimal total;
	
	@Column(name="direccionEnvio", length=100)
	private String direccionEnvio;
	
	@Column(length=100)
	private String referencia;
	
	@Column(name="latitud_envio", precision=10, scale=8)
	private BigDecimal latitudEnvio;
	
	@Column(name="longitud_envio", precision=10, scale=8)
	private BigDecimal longitudEnvio;
	
	@Column(name="fecha_hora", insertable=false, updatable=false)
	private LocalDateTime fechaHora;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="idUsuario", nullable=false)
	@ToString.Exclude 
	private Usuario cliente;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="idNegocio", nullable=false)
	@ToString.Exclude 
	private Negocio negocio;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="idRepartidor") // Puede ser nulo cuando se crea
	@ToString.Exclude 
	private Usuario repartidor;

}