package com.proyecto.repository;

import com.proyecto.model.Producto;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

  List<Producto> findByNegocioIdNegocioAndDisponibleTrue(Integer idNegocio);

  // NUEVO: buscar productos dentro de un restaurante
  @Query(
      """
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
      @Param("idNegocio") Integer idNegocio, @Param("texto") String texto);
  
  @EntityGraph(
	        attributePaths = {
	            "categoriaProducto"
	        }
	    )
	    Page<Producto> findByNegocio_IdNegocio(
	        Integer idNegocio,
	        Pageable pageable
	    );


	    @EntityGraph(
	        attributePaths = {
	            "categoriaProducto"
	        }
	    )
	    Page<Producto>
	    findByNegocio_IdNegocioAndNombreContainingIgnoreCase(
	        Integer idNegocio,
	        String nombre,
	        Pageable pageable
	    );
}
