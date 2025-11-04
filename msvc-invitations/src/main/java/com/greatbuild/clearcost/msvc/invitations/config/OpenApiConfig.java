package com.greatbuild.clearcost.msvc.invitations.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI/Swagger para msvc-invitations
 * Incluye configuración de seguridad JWT Bearer Token
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "ClearCost Invitations API", // <-- CAMBIO
                version = "1.0",
                description = "API para la gestión de invitaciones a organizaciones. " + // <-- CAMBIO
                        "Permite a los usuarios aceptar o rechazar invitaciones pendientes. " +
                        "Autenticación mediante JWT Bearer Token emitido por msvc-users.",
                contact = @Contact(
                        name = "ClearCost Platform Team",
                        email = "support@clearcost.com"
                )
        ),
        servers = {
                @Server(
                        url = "http://localhost:8004", // <-- CAMBIO (Este es el puerto de invitations)
                        description = "Servidor de Desarrollo"
                ),
                @Server(
                        url = "https://api.clearcost.com",
                        description = "Servidor de Producción"
                )
        }
)
@SecurityScheme(
        name = "Bearer Authentication", // <-- Este nombre es el estándar para toda tu plataforma
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Ingresa el JWT token obtenido desde msvc-users (/api/auth/login). " + // <-- CAMBIO
                "Se requiere autenticación para crear, aceptar o rechazar invitaciones."
)
public class OpenApiConfig {
}
