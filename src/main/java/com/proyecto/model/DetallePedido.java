package com.proyecto.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.math.BigDecimal;

@Entity
@Table(name="DETALLE_PEDIDO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedido {
	
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Integer idDetallePedido;
	
	private Integer cantidad;
	
	@Column(name="precioUnitario", precision=10, scale=2)
	private BigDecimal precioUnitario;
	
	@Column(precision=10, scale=2)
	private BigDecimal subtotal;
	
	@Column(name="notaEspecial", length=100)
	private String notaEspecial;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="idPedido", nullable=false)
	@ToString.Exclude 
	private Pedido pedido;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="idProducto", nullable=false)
	@ToString.Exclude 
	private Producto producto;

}