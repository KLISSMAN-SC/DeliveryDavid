package com.proyecto.repository;

import com.proyecto.model.TipoNegocio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoNegocioRepository extends JpaRepository<TipoNegocio, Integer> {
	TipoNegocio findByNombreIgnoreCase(String nombre);
}