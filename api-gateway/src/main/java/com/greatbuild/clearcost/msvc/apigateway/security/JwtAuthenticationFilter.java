package com.greatbuild.clearcost.msvc.apigateway.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Filtro global de Gateway para validar el JWT y propagar información del usuario autenticado.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JwtAuthenticationFilter implements GlobalFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final RouteValidator routeValidator;

    public JwtAuthenticationFilter(JwtService jwtService, RouteValidator routeValidator) {
        this.jwtService = jwtService;
        this.routeValidator = routeValidator;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Permitir preflight CORS
        if (request.getMethod() != null && "OPTIONS".equalsIgnoreCase(request.getMethod().name())) {
            return chain.filter(exchange);
        }

        if (!routeValidator.isSecured.test(request)) {
            return chain.filter(exchange);
        }

        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            log.debug("Solicitud sin encabezado Authorization para ruta protegida: {}", request.getURI());
            return this.onError(exchange, HttpStatus.UNAUTHORIZED, "Token no encontrado en la cabecera Authorization");
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return this.onError(exchange, HttpStatus.UNAUTHORIZED, "Encabezado Authorization inválido");
        }

        String token = authHeader.substring(7);
        if (!jwtService.isTokenValid(token)) {
            log.debug("Token JWT inválido o expirado");
            return this.onError(exchange, HttpStatus.UNAUTHORIZED, "Token inválido o expirado");
        }

        Long userId;
        try {
            userId = jwtService.extractUserId(token);
        } catch (Exception ex) {
            log.debug("No se pudo extraer userId del token: {}", ex.getMessage());
            return this.onError(exchange, HttpStatus.UNAUTHORIZED, "Token inválido");
        }

        List<String> roles = jwtService.extractRoles(token);
        String rolesHeader = roles != null ? String.join(",", roles) : "";

        ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-Authenticated-UserId", userId != null ? userId.toString() : "")
                .header("X-Authenticated-Roles", rolesHeader)
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] bytes = ("{\"error\":\"" + message + "\"}").getBytes(StandardCharsets.UTF_8);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
    }
}
