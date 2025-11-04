package com.greatbuild.clearcost.msvc.users.controllers;

import com.greatbuild.clearcost.msvc.users.models.entities.User;
import com.greatbuild.clearcost.msvc.users.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controlador para endpoints internos (comunicación entre microservicios)
 * Estos endpoints NO requieren autenticación JWT
 * ⚠️ NO deben exponerse públicamente en producción (usar API Gateway para filtrar)
 */
@RestController
@RequestMapping("/api/users/internal")
@Tag(name = "Internal Users", description = "Endpoints internos para comunicación entre microservicios (SIN autenticación)")
public class InternalUserController {

    private final UserService userService;

    public InternalUserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Obtiene un usuario por su ID (SIN autenticación)
     * Usado por msvc-invitations y msvc-organizations
     * 
     * @param id ID del usuario
     * @return Usuario encontrado o 404
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener usuario por ID (interno)",
            description = "Endpoint INTERNO para consultar información de un usuario. NO requiere JWT."
    )
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        Optional<User> userOptional = userService.findById(id);
        return userOptional.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Busca un usuario por email (SIN autenticación)
     * Usado por otros microservicios
     * 
     * @param email Email del usuario
     * @return Usuario encontrado o 404
     */
    @GetMapping("/email/{email}")
    @Operation(
            summary = "Buscar usuario por email (interno)",
            description = "Endpoint INTERNO para buscar usuario por email. NO requiere JWT."
    )
    public ResponseEntity<User> getUserByEmail(@PathVariable("email") String email) {
        Optional<User> userOptional = userService.findByEmail(email);
        return userOptional.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
