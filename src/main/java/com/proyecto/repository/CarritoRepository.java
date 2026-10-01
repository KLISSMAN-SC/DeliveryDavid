package com.proyecto.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.model.Carrito;


@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Integer> {

	Optional<Carrito> findByUsuarioIdUsuarioAndNegocioIdNegocioAndEstado(
            Integer idUsuario,
            Integer idNegocio,
            String estado
    );
}
