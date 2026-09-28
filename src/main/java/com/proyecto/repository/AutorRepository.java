package com.proyecto.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.model.Autor;

@Repository
public interface AutorRepository extends JpaRepository<Autor, Integer>{
	List<Autor> findByNombreContainingIgnoreCase(String nombre);
}
