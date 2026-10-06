package com.proyecto.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        
        // Esto genera automáticamente la ruta absoluta correcta sin importar si estás en Windows (C:/...) o en Linux (/opt/...)
        String rutaBase = Paths.get("RIDE_MEAL").toAbsolutePath().toUri().toString();
        
        registry.addResourceHandler("/imagenes/**")
                .addResourceLocations(rutaBase + "imagenes/"); 
        
        registry.addResourceHandler("/categorias_comidas/**")
                .addResourceLocations(rutaBase + "categorias_comidas/");
        
        registry.addResourceHandler("/banner/**")
                .addResourceLocations(rutaBase + "banner/");
        
        registry.addResourceHandler("/productos/**")
                .addResourceLocations(rutaBase + "productos/");
        
        registry.addResourceHandler("/promociones/**")
        		.addResourceLocations(rutaBase + "promociones/");
    }
}