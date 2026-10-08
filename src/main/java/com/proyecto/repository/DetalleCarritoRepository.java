package com.proyecto.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.proyecto.model.DetalleCarrito;

@Repository
public interface DetalleCarritoRepository extends JpaRepository<DetalleCarrito, Integer> {
	
	Optional<DetalleCarrito> findByCarritoIdCarritoAndProductoIdProducto(
            Integer idCarrito,
            Integer idProducto
    );
	
	@Modifying(
		    flushAutomatically = true,
		    clearAutomatically = true
		)
		@Query(
		    value = """
		        INSERT INTO DETALLE_CARRITO
		        (
		            idCarrito,
		            idProducto,
		            cantidad,
		            precioUnitario
		        )
		        VALUES
		        (
		            :idCarrito,
		            :idProducto,
		            1,
		            :precioUnitario
		        )

		        ON DUPLICATE KEY UPDATE

		        cantidad = cantidad + 1,
		        precioUnitario = :precioUnitario
		        """,
		    nativeQuery = true
		)
		void agregarOIncrementar(

		    @Param("idCarrito")
		    Integer idCarrito,

		    @Param("idProducto")
		    Integer idProducto,

		    @Param("precioUnitario")
		    BigDecimal precioUnitario
		);
}
