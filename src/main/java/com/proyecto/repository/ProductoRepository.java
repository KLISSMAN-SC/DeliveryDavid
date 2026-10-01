package com.proyecto.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.proyecto.model.Producto;
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {


	List<Producto> findByNegocioIdNegocioAndDisponibleTrue(Integer idNegocio);
	
	// NUEVO: buscar productos dentro de un restaurante
    @Query("""
        SELECT p
        FROM Producto p
        WHERE p.negocio.idNegocio = :idNegocio
        AND p.disponible = true
        AND (
            LOWER(p.nombre)
                LIKE LOWER(CONCAT('%', :texto, '%'))

            OR LOWER(COALESCE(p.descripcion, ''))
                LIKE LOWER(CONCAT('%', :texto, '%'))
        )
        ORDER BY p.nombre
    """)
    List<Producto> buscarProductosPorNegocio(
            @Param("idNegocio") Integer idNegocio,
            @Param("texto") String texto
    );
}
