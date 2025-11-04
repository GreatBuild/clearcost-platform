package com.greatbuild.clearcost.msvc.users.controllers;

import com.greatbuild.clearcost.msvc.users.models.entities.User;
import com.greatbuild.clearcost.msvc.users.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controlador para operaciones relacionadas con usuarios
 * Endpoints protegidos con JWT
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Gestión de usuarios")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Obtiene un usuario por su ID
     * Endpoint usado por otros microservicios (msvc-organizations)
     * 
     * @param id ID del usuario
     * @return Usuario encontrado o 404
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener usuario por ID",
            description = "Endpoint para consultar información de un usuario por su ID. Usado por msvc-organizations para validar ownerId."
    )
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        Optional<User> userOptional = userService.findById(id);
        return userOptional.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Busca un usuario por email
     * 
     * @param email Email del usuario
     * @return Usuario encontrado o 404
     */
    @GetMapping("/email/{email}")
    @Operation(summary = "Buscar usuario por email", 
               description = "Busca un usuario por su dirección de email")
    public ResponseEntity<User> getUserByEmail(@PathVariable("email") String email) {
        Optional<User> userOptional = userService.findByEmail(email);
        return userOptional.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
