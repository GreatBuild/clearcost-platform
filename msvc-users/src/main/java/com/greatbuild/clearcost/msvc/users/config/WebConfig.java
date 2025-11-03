package com.greatbuild.clearcost.msvc.users.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Permite CORS para todos tus endpoints /api

                // Permitimos el origen de tu Frontend (Angular)
                // Y también el origen de tu Backend (para que Swagger UI funcione)
                .allowedOrigins("http://localhost:4200", "http://localhost:8003")

                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true); // Necesario para el flujo de Google
    }
}