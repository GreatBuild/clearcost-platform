package com.greatbuild.clearcost.msvc.invitations.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Interceptor para propagar el JWT en las peticiones de Feign Client
 * Extrae el token del SecurityContext y lo agrega al header Authorization
 * EXCEPTO para endpoints internos (/internal/) que no requieren autenticación
 */
@Component
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        // NO agregar JWT a endpoints internos (ej: /api/users/internal/*)
        String url = requestTemplate.url();
        if (url.contains("/internal/") || url.contains("/internal")) {
            // Endpoint interno - NO agregar JWT
            return;
        }

        // Para endpoints normales, agregar JWT
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getCredentials() != null) {
            // Si el token está en credentials
            String token = authentication.getCredentials().toString();
            requestTemplate.header("Authorization", "Bearer " + token);
        } else {
            // Intentar obtener el token del request actual
            // Esto es útil cuando el token no está en credentials
            // pero sí en el contexto del request HTTP
            String authHeader = getAuthorizationHeaderFromContext();
            if (authHeader != null) {
                requestTemplate.header("Authorization", authHeader);
            }
        }
    }

    private String getAuthorizationHeaderFromContext() {
        // Obtener el header Authorization del request actual si está disponible
        try {
            org.springframework.web.context.request.RequestAttributes requestAttributes = 
                org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
            
            if (requestAttributes instanceof org.springframework.web.context.request.ServletRequestAttributes) {
                jakarta.servlet.http.HttpServletRequest request = 
                    ((org.springframework.web.context.request.ServletRequestAttributes) requestAttributes).getRequest();
                return request.getHeader("Authorization");
            }
        } catch (Exception e) {
            // Si no hay contexto de request, devolver null
        }
        return null;
    }
}
