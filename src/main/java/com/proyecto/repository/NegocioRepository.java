package com.proyecto.repository;

import com.proyecto.model.Negocio;
import com.proyecto.model.TipoNegocio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NegocioRepository extends JpaRepository<Negocio, Integer> {
	List<Negocio> findByNombreContainingIgnoreCase(String nombre);
	List<Negocio> findByTipoNegocio(TipoNegocio tipoNegocio);
}