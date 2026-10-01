package com.proyecto.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.model.DetalleCarrito;

@Repository
public interface DetalleCarritoRepository extends JpaRepository<DetalleCarrito, Integer> {
	
	Optional<DetalleCarrito> findByCarritoIdCarritoAndProductoIdProducto(
            Integer idCarrito,
            Integer idProducto
    );
}
