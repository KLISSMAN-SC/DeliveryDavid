package com.proyecto.services;

import com.proyecto.model.Zona;
import com.proyecto.repository.ZonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ZonaService {
    @Autowired
    private ZonaRepository zonaRepository;

    public List<Zona> obtenerTodas() {
        return zonaRepository.findAll();
    }
}