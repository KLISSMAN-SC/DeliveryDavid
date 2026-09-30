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
	
	public enum EstadoPedido {PENDIENTE_DE_WHATSAPP,CONFIRMADO,EN_CAMINO,ENTREGADO,CANCELADO}
	
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Integer idPedido;
	
	
	@Enumerated(EnumType.STRING)
	@Column(name = "estadoPedido", length = 10)
	private EstadoPedido estadoPedido;
	
	@Column(name="metodoPago", length=45)
	private String metodoPago;
	
	@Column(name="costoEnvio", precision=10, scale=2)
	private BigDecimal costoEnvio;
	
	@Column(precision=10, scale=2)
	private BigDecimal propina;
	
	@Column(precision=10, scale=2)
	private BigDecimal total;
	
	@Column(name="direccionEnvio", length=100)
	private String direccionEnvio;
	
	@Column(length=100)
	private String referencia;
	
	@Column(name="latitudEnvio", precision=10, scale=8)
	private BigDecimal latitudEnvio;
	
	@Column(name="longitudEnvio", precision=10, scale=8)
	private BigDecimal longitudEnvio;
	
	@Column(name="fechaHora", insertable=false, updatable=false)
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