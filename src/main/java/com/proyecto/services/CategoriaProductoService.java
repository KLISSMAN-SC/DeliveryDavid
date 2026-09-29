package com.proyecto.services;

import com.proyecto.repository.CategoriaProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoriaProductoService {
    
    @Autowired
    private CategoriaProductoRepository categoriaProductoRepository;

    public List<String> obtenerCategoriasUnicas() {
        return categoriaProductoRepository.findNombresUnicos();
    }
}