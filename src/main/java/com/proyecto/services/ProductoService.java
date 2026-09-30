package com.proyecto.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.model.Producto;
import com.proyecto.repository.NegocioRepository;
import com.proyecto.repository.ProductoRepository;

@Service
public class ProductoService {

	@Autowired
	private ProductoRepository productoRepository;
	
	
    public List<Producto> listarProductosPorNegocio(Integer idNegocio) {

        return productoRepository.findByNegocioIdNegocioAndDisponibleTrue(idNegocio);

    }
}
