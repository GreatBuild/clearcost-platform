package com.greatbuild.clearcost.msvc.users.security;

import com.greatbuild.clearcost.msvc.users.models.entities.User;
import com.greatbuild.clearcost.msvc.users.services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.stream.Collectors;

/**
 * Handler que maneja el éxito de la autenticación OAuth2.
 * Genera el JWT y redirige al frontend con el token en la URL.
 */
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserService userService;

    private static final String FRONTEND_CALLBACK_URL = "http://localhost:4200/login";

    public OAuth2AuthenticationSuccessHandler(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            OAuth2User oauthUser = oauthToken.getPrincipal();
            
            String email = oauthUser.getAttribute("email");
            
            if (email == null || email.isBlank()) {
                // Redirigir al frontend con error
                String errorUrl = UriComponentsBuilder.fromUriString(FRONTEND_CALLBACK_URL)
                        .queryParam("error", "no_email")
                        .build()
                        .toUriString();
                getRedirectStrategy().sendRedirect(request, response, errorUrl);
                return;
            }

            // Buscar el usuario en la BD (ya fue guardado/actualizado por CustomOAuth2UserService)
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado después de OAuth2"));

            // Verificar si necesita seleccionar rol
            boolean needsRoleSelection = user.getRoles().stream()
                    .anyMatch(role -> role.getName().equals("ROLE_PENDING_SELECTION"));

            // Crear Authentication con roles del usuario
            org.springframework.security.authentication.UsernamePasswordAuthenticationToken jwtAuthentication = 
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                    user.getEmail(),
                    null,
                    user.getRoles().stream()
                            .map(role -> new SimpleGrantedAuthority(role.getName()))
                            .collect(Collectors.toSet())
                );

            // Generar JWT
            String jwt = jwtService.generateToken(user.getId(), jwtAuthentication);

            // Construir URL de redirección al frontend con parámetros
            String targetUrl = UriComponentsBuilder.fromUriString(FRONTEND_CALLBACK_URL)
                    .queryParam("token", jwt)
                    .queryParam("email", user.getEmail())
                    .queryParam("needsRoleSelection", needsRoleSelection)
                    .build()
                    .toUriString();

            // Redirigir al frontend
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        } else {
            // Fallback en caso de que no sea OAuth2
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }
}
