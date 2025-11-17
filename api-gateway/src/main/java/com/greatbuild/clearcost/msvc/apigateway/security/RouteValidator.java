package com.greatbuild.clearcost.msvc.apigateway.security;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.function.Predicate;

/**
 * Define qué rutas son públicas (no requieren autenticación).
 */
@Component
public class RouteValidator {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/me",
            "oauth2/authorization/google",
            "/api/users/internal/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/actuator/**"
    );

    public final Predicate<ServerHttpRequest> isSecured = request -> PUBLIC_ENDPOINTS.stream()
            .noneMatch(uri -> PATH_MATCHER.match(uri, request.getURI().getPath()));
}
