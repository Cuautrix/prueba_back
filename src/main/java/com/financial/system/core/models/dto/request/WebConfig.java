package com.financial.system.core.models.dto.request;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ruta absoluta al directorio "storage"
        String storagePath = Paths.get("storage").toAbsolutePath().toUri().toString();

        // Mapea cualquier URL que comience con /storage/** a la carpeta física "storage/"
        registry.addResourceHandler("/storage/**")
                .addResourceLocations(storagePath);
    }
}
