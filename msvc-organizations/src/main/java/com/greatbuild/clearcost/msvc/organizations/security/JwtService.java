package com.greatbuild.clearcost.msvc.organizations.security;

import com.greatbuild.clearcost.msvc.organizations.config.JwtProperties;
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

    // Extrae el email del token (para tokens OAuth2, está en el claim "email")
    public String extractUsername(String token) {
        Claims claims = extractAllClaims(token);
        String email = claims.get("email", String.class);
        return (email != null) ? email : claims.getSubject();
    }

    // Extrae el userId del token (está en el subject como String para tokens OAuth2)
    public Long extractUserId(String token) {
        String subject = extractClaim(token, Claims::getSubject);
        try {
            // Para tokens OAuth2, el subject es el userId
            return Long.parseLong(subject);
        } catch (NumberFormatException e) {
            // Para tokens antiguos, el subject puede ser el email
            // En ese caso, no podemos extraer el userId directamente
            throw new IllegalArgumentException("userId no encontrado o inválido en el subject del token JWT: " + subject);
        }
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
