package com.proyecto.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String paginaPrincipal() {
        // Esto le indica a Spring Boot que busque un archivo llamado "index.html"
        return "index"; 
    }
}