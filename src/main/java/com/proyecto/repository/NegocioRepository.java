package com.proyecto.repository;

import com.proyecto.model.Negocio;
import com.proyecto.model.TipoNegocio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NegocioRepository extends JpaRepository<Negocio, Integer> {


    List<Negocio> findByNombreContainingIgnoreCase(String nombre);

    Negocio findByIdNegocio(Integer idNegocio);
    
    List<Negocio> findByTipoNegocioIdTipoNegocio(Integer idTipoNegocio);
    
    List<Negocio>findByTipoNegocio(TipoNegocio tipoNegocio);

    
 // NUEVO: Consulta maestra que combina Tipo, Zona y Categoría dinámicamente
    @Query(value = "SELECT DISTINCT n.* FROM NEGOCIO n " +
            "LEFT JOIN CATEGORIAS_PRODUCTO c ON n.idNegocio = c.idNegocio " +
            "WHERE (:tipoId = -1 OR n.idTipoNegocio = :tipoId) " +
            "AND (:zonaId = -1 OR n.idZona = :zonaId) " +
            "AND (:categoria = '' OR c.nombre = :categoria)", 
            nativeQuery = true)
    List<Negocio> findByFiltrosCombinados(@Param("tipoId") Integer tipoId, @Param("zonaId") Integer zonaId, @Param("categoria") String categoria);
}