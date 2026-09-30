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



    public List<Negocio> obtenerTodos(){

        return negocioRepository.findAll();

    }

    public List<Negocio> buscarPorNombre(String nombre){

        if(nombre == null || nombre.trim().isEmpty()){

            return List.of();
        }
        
        return negocioRepository.findByNombreContainingIgnoreCase(nombre.trim());
    }

    public List<Negocio> listarPorTipo(Integer idTipoNegocio){

        return negocioRepository.findByTipoNegocioIdTipoNegocio(idTipoNegocio);
    }
    
 // Agrégalo debajo de los demás métodos en NegocioService.java
    public List<Negocio> obtenerPorTipo(TipoNegocio tipoNegocio) {
        return negocioRepository.findByTipoNegocio(tipoNegocio);
    }
    public List<Negocio> buscarConFiltrosCombinados(Integer tipoId, Integer zonaId, String categoria) {
        // Reemplazamos los nulos por valores que no existen (-1 o vacío) para evitar el error de MySQL
        Integer filtroTipo = (tipoId == null) ? -1 : tipoId;
        Integer filtroZona = (zonaId == null) ? -1 : zonaId;
        String filtroCat = (categoria == null) ? "" : categoria;
        
        return negocioRepository.findByFiltrosCombinados(filtroTipo, filtroZona, filtroCat);
    }
    

}