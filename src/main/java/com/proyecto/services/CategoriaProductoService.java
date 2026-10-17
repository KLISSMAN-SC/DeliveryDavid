package com.proyecto.services;

import com.proyecto.model.CategoriaProducto;
import com.proyecto.repository.CategoriaProductoRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoriaProductoService {

  @Autowired private CategoriaProductoRepository categoriaProductoRepository;

  public List<String> obtenerCategoriasUnicas() {
    return categoriaProductoRepository.findNombresUnicos();
  }
  
  public List<CategoriaProducto> listarPorNegocio(Integer idNegocio){
	  return categoriaProductoRepository.findByNegocio_IdNegocioOrderByNombreAsc(idNegocio);
  }
}
