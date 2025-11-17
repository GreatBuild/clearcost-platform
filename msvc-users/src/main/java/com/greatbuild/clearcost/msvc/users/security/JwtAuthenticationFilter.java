package com.greatbuild.clearcost.msvc.users.security;

import com.greatbuild.clearcost.msvc.users.services.UserServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // Le dice a Spring que cree un bean de esta clase
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService; // Este es tu UserServiceImpl

    // ¡Inyección por Constructor!
    public JwtAuthenticationFilter(JwtService jwtService, UserServiceImpl userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // SKIP JWT validation para endpoints internos (comunicación entre microservicios)
        String requestPath = request.getRequestURI();
        if (requestPath.startsWith("/api/users/internal/")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 1. Si no hay token o no es Bearer, lo ignoramos y seguimos
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extraemos el token
        jwt = authHeader.substring(7); // Quita el "Bearer "

        try {
            // 3. Extraemos el email del token
            userEmail = jwtService.extractUsername(jwt);

            // 4. Si tenemos email Y el usuario no está ya autenticado
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 5. Cargamos el usuario de la BD (¡Aquí se llama al UserServiceImpl.loadUserByUsername!)
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // 6. Si el token es válido...
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // 7. Creamos la autenticación y la guardamos en el Contexto de Seguridad
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null, // No usamos credenciales (password)
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // ¡Este es el paso clave! El usuario queda "logueado" para esta petición
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Error al procesar el token JWT: {}", e.getMessage(), e);
            // Si el token es inválido (expirado, firma incorrecta),
            // limpiamos el contexto y continuamos sin autenticación
            // Esto permite que Spring Security maneje el error adecuadamente
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
        }
    }
}