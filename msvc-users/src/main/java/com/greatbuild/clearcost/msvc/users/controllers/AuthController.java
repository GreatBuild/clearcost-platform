package com.greatbuild.clearcost.msvc.users.controllers;

import com.greatbuild.clearcost.msvc.users.models.dtos.AuthResponseDTO;
import com.greatbuild.clearcost.msvc.users.models.dtos.LoginRequestDTO;
import com.greatbuild.clearcost.msvc.users.models.dtos.RegisterRequestDTO;
import com.greatbuild.clearcost.msvc.users.models.dtos.RoleSelectionDTO;
import com.greatbuild.clearcost.msvc.users.models.entities.User;
import com.greatbuild.clearcost.msvc.users.security.JwtService;
import com.greatbuild.clearcost.msvc.users.services.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
// ¡Esta config de CORS ahora la maneja WebConfig.java!
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    // ¡Inyección por Constructor!
    public AuthController(UserService userService,
                          AuthenticationManager authenticationManager,
                          JwtService jwtService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    // --- Endpoint PÚBLICO: Login Local ---
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Obtenemos el userId del usuario autenticado
            User user = userService.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            String jwt = jwtService.generateToken(user.getId(), authentication);
            return ResponseEntity.ok(new AuthResponseDTO(jwt));
        } catch (AuthenticationException e) {
            log.warn("¡FALLO DE LOGIN! Email o password incorrecto para {}: {}", loginRequest.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Email o password incorrecto"));
        }
    }

    // --- Endpoint PÚBLICO: Registro Local ---
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        try {
            User user = userService.registerNewUser(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Registro exitoso",
                            "email", user.getEmail()
                    ));
        } catch (RuntimeException e) {
            log.warn("Error en registro: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /* 
     * NOTA: Este endpoint ya no es usado por el flujo OAuth2.
     * El handler OAuth2AuthenticationSuccessHandler redirige directamente al frontend.
     * Se mantiene comentado por si se necesita en el futuro para debugging.
     * 
    // --- Endpoint PÚBLICO: Callback de Google ---
    @GetMapping("/oauth-success")
    public ResponseEntity<?> oauthSuccess(Authentication authentication) {
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            // Redirigir al frontend con error
            String errorUrl = "http://localhost:4200/auth/callback?error=unauthorized";
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", errorUrl)
                    .build();
        }

        String email = getEmailFromAuthentication(authentication);
        if (email == null) {
            // Redirigir al frontend con error
            String errorUrl = "http://localhost:4200/auth/callback?error=no_email";
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", errorUrl)
                    .build();
        }

        // Buscamos el usuario en la BD
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado en BD después de OAuth2"));

        // Verificamos si necesita seleccionar rol
        boolean needsRoleSelection = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_PENDING_SELECTION"));

        // Creamos un Authentication con el email como principal
        Authentication jwtAuthentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null,
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toSet())
        );

        // Generamos el JWT con userId
        String jwt = jwtService.generateToken(user.getId(), jwtAuthentication);

        // Redirigir al frontend con el token y parámetros necesarios
        String frontendUrl = "http://localhost:4200/auth/callback" +
                "?token=" + jwt +
                "&email=" + user.getEmail() +
                "&needsRoleSelection=" + needsRoleSelection;

        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", frontendUrl)
                .build();
    }
    */

    // --- Endpoint PRIVADO: Selección de Rol ---
    @PostMapping("/select-role")
    public ResponseEntity<?> selectRole(@Valid @RequestBody RoleSelectionDTO roleDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = getEmailFromAuthentication(authentication);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Usuario no autenticado o email no encontrado."));
        }

        try {
            User user = userService.updateUserRole(email, roleDto.getRoleName());
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            String errorMessage = (e.getMessage() == null) ? "Error desconocido: " + e.getClass().getName() : e.getMessage();
            return ResponseEntity.badRequest().body(Map.of("error", errorMessage));
        }
    }

    // --- Endpoint PRIVADO: Ver mi perfil ---
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        String email = getEmailFromAuthentication(authentication);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Usuario no autenticado o email no encontrado."));
        }

        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado en la BD."));

        return ResponseEntity.ok(user);
    }

    // --- Método de Ayuda (Helper) ---
    private String getEmailFromAuthentication(Authentication authentication) {
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }

        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
            OAuth2User oauthUser = token.getPrincipal();
            return oauthUser.getAttribute("email");
        } else {
            // Para login local Y para peticiones JWT, el "name" es el email
            return authentication.getName();
        }
    }
}
