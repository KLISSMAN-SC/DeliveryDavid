package com.proyecto.config;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.resource.PathResourceResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {
  private final String root;

  public WebConfig(@Value("${app.storage-root:RIDE_MEAL}") String root) {
    this.root = root;
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    for (String bucket :
        new String[] {
          "imagenes", "categorias_comidas", "banner", "productos", "promociones", "carta"
        }) {
      String location = Paths.get(root, bucket).toAbsolutePath().normalize().toUri().toString();
      if (!location.endsWith("/")) location += "/";
      registry
          .addResourceHandler("/" + bucket + "/**")
          .addResourceLocations(location)
          .resourceChain(true)
          .addResolver(
              new PathResourceResolver() {
                @Override
                protected Resource getResource(String path, Resource location) throws IOException {
                  if (path.contains("/")
                      || path.contains("\\")
                      || !path.toLowerCase(Locale.ROOT).matches("[^/]+\\.(png|jpe?g|gif|webp|ico)"))
                    return null;
                  return super.getResource(path, location);
                }
              });
    }
  }
}
