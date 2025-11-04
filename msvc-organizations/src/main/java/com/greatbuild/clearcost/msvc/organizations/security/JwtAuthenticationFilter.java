package com.greatbuild.clearcost.msvc.organizations.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Filtro JWT STATELESS para msvc-organizations
 * 
 * Arquitectura "JWT Pasaporte":
 * - NO consulta la base de datos
 * - Confía en el JWT firmado por msvc-users
 * - Extrae los roles directamente del token
 * - Valida solo: firma + expiración
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        final List<String> roles;

        // 1. Si no hay token Bearer, continuar sin autenticación
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extraer el token
        jwt = authHeader.substring(7);

        try {
            // 3. Validar firma y expiración (STATELESS - sin BD)
            if (!jwtService.isTokenValid(jwt)) {
                log.warn("Token JWT inválido o expirado");
                SecurityContextHolder.clearContext();
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token JWT inválido o expirado");
                return;
            }

            // 4. Extraer email y roles del token (confiar en el JWT)
            userEmail = jwtService.extractUsername(jwt);
            roles = jwtService.extractRoles(jwt);

            // 5. Si ya está autenticado, no hacer nada
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                log.info("✅ Token JWT válido para usuario: {} con roles: {}", userEmail, roles);

                // 6. Crear autenticación con los roles del JWT (sin consultar BD)
                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userEmail,  // Principal: el email del usuario
                        null,       // Credentials: no necesitamos password
                        authorities // Authorities: roles extraídos del JWT
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 7. Establecer autenticación en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("Error al procesar el token JWT: {}", e.getMessage(), e);
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token JWT inválido");
        }
    }
}
