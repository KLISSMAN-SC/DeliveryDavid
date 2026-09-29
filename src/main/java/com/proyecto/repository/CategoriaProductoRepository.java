package com.proyecto.repository;

import com.proyecto.model.CategoriaProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategoriaProductoRepository extends JpaRepository<CategoriaProducto, Integer> {
    
    // Extrae solo los nombres de categoría eliminando los repetidos
    @Query("SELECT DISTINCT c.nombre FROM CategoriaProducto c")
    List<String> findNombresUnicos();
}