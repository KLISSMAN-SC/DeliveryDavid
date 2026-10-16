package com.proyecto.services;

import com.proyecto.model.TipoNegocio;
import com.proyecto.repository.TipoNegocioRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TipoNegocioService {

  @Autowired private TipoNegocioRepository tipoNegocioRepository;

  public List<TipoNegocio> obtenerTodos() {
    return tipoNegocioRepository.findAll();
  }

  public TipoNegocio obtenerPorId(Integer id) {
    return tipoNegocioRepository.findById(id).orElse(null);
  }

  public TipoNegocio obtenerPorNombre(String nombre) {

    return tipoNegocioRepository.findByNombreIgnoreCase(nombre);
  }
}
