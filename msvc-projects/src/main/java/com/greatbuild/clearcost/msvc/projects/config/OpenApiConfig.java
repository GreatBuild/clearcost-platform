package com.greatbuild.clearcost.msvc.projects.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "ClearCost Projects API",
                version = "1.0",
                description = "API para la gestión de proyectos de construcción",
                contact = @Contact(
                        name = "GreatBuild Team",
                        email = "support@greatbuild.com"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8005", description = "Local Server")
        }
)
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenApiConfig {
}
