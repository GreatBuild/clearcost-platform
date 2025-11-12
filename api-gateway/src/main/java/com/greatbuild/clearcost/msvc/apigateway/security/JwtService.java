package com.greatbuild.clearcost.msvc.apigateway.security;

import com.greatbuild.clearcost.msvc.apigateway.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

/**
 * Servicio utilitario para validar y extraer información del JWT.
 */
@Component
public class JwtService {

    private final JwtProperties properties;
    private Key key;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
    }

    /**
     * Verifica si el token es válido.
     */
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Date expiration = claims.getExpiration();
            return expiration == null || expiration.after(new Date());
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Extrae el userId (subject) del token.
     */
    public Long extractUserId(String token) {
        String subject = extractClaim(token, Claims::getSubject);
        return subject != null ? Long.parseLong(subject) : null;
    }

    /**
     * Extrae los roles del token.
     */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("roles", List.class);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Key getSigningKey() {
        if (key == null) {
            byte[] keyBytes = Decoders.BASE64.decode(properties.getSecret());
            key = Keys.hmacShaKeyFor(keyBytes);
        }
        return key;
    }
}
