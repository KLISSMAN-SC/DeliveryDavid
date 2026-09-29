package com.proyecto.services;

import com.proyecto.model.Negocio;
import com.proyecto.model.TipoNegocio;
import com.proyecto.repository.NegocioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NegocioService {
    
    @Autowired
    private NegocioRepository negocioRepository;

    public List<Negocio> obtenerTodos() {
        return negocioRepository.findAll();
    }
    
    public List<Negocio> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return List.of();
        }
        return negocioRepository.findByNombreContainingIgnoreCase(nombre.trim());
    }
    
    public List<Negocio> obtenerPorTipo(TipoNegocio tipoNegocio){

        return negocioRepository.findByTipoNegocio(tipoNegocio);

    }
    
}