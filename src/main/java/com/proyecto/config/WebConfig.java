package com.proyecto.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Todo lo que empiece con /imagenes/ en el HTML...
        // ...Spring Boot lo buscará en esta carpeta física externa de tu computadora/servidor:
        registry.addResourceHandler("/imagenes/**")
                .addResourceLocations("file:///C:/RIDE_MEAL/imagenes/"); 
        
        registry.addResourceHandler("/categorias_comidas/**")
        .addResourceLocations("file:///C:/RIDE_MEAL/categorias_comidas/");
        
        registry.addResourceHandler("/banner/**")
        .addResourceLocations("file:///C:/RIDE_MEAL/banner/");
        
    }
}