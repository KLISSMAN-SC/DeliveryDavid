package com.proyecto.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.proyecto.model.Producto;
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {


	List<Producto> findByNegocioIdNegocioAndDisponibleTrue(Integer idNegocio);


}
