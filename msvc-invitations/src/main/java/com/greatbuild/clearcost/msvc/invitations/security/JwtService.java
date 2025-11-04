package com.greatbuild.clearcost.msvc.invitations.security;

import com.greatbuild.clearcost.msvc.invitations.config.JwtProperties;
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
 * Servicio JWT STATELESS para msvc-organizations
 * NO consulta la base de datos - confía en el JWT firmado por msvc-users
 */
@Component
public class JwtService {

    private final String jwtSecret;
    private final int jwtExpirationMs;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtSecret = jwtProperties.getSecret();
        this.jwtExpirationMs = jwtProperties.getExpirationMs();
    }

    // Extrae el userId (subject) del token
    public Long extractUserId(String token) {
        String subject = extractClaim(token, Claims::getSubject);
        return Long.parseLong(subject);
    }

    // Extrae el email del token (desde el claim "email")
    public String extractEmail(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("email", String.class);
    }

    // Extrae el username (subject) del token - mantener por compatibilidad
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extrae los roles del token (la clave del patrón "JWT Pasaporte")
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("roles", List.class);
    }

    // Valida el token de forma STATELESS (sin consultar BD)
    // Solo verifica: firma correcta + no expirado
    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token); // Si lanza excepción, firma incorrecta
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Parsea y valida la firma del token
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
