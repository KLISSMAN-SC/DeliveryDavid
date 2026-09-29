package com.proyecto.services;

import com.proyecto.model.TipoNegocio;
import com.proyecto.repository.TipoNegocioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TipoNegocioService {

    @Autowired
    private TipoNegocioRepository tipoNegocioRepository;

    public List<TipoNegocio> obtenerTodos() {
        return tipoNegocioRepository.findAll();
    }
}