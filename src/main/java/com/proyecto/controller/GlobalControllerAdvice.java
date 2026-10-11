package com.proyecto.controller;

import com.proyecto.model.Configuracion;
import com.proyecto.repository.ConfiguracionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private ConfiguracionRepository configRepo;

    @ModelAttribute("configApp")
    public Configuracion globalConfig() {
        // Busca la configuración con ID 1. Si no existe, devuelve una en blanco.
        return configRepo.findById(1).orElse(new Configuracion());
    }
}