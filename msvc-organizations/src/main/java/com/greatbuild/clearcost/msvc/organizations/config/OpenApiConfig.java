package com.greatbuild.clearcost.msvc.organizations.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI/Swagger para msvc-organizations
 * Incluye configuración de seguridad JWT Bearer Token
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "ClearCost Organizations API",
        version = "1.0",
        description = "API para gestión de organizaciones y miembros en ClearCost Platform. " +
                      "Autenticación mediante JWT Bearer Token emitido por msvc-users.",
        contact = @Contact(
            name = "ClearCost Platform Team",
            email = "support@clearcost.com"
        )
    ),
    servers = {
        @Server(
            url = "http://localhost:8002",
            description = "Servidor de Desarrollo"
        ),
        @Server(
            url = "https://api.clearcost.com",
            description = "Servidor de Producción"
        )
    }
)
@SecurityScheme(
    name = "Bearer Authentication",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "Ingresa el JWT token obtenido desde msvc-users (/api/auth/login). " +
                  "El token debe incluir el rol 'ROLE_WORKER' para crear organizaciones."
)
public class OpenApiConfig {
}
