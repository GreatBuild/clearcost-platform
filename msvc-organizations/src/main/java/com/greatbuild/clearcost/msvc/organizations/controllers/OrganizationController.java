package com.greatbuild.clearcost.msvc.organizations.controllers;

import com.greatbuild.clearcost.msvc.organizations.models.dtos.CreateOrganizationDTO;
import com.greatbuild.clearcost.msvc.organizations.models.dtos.OrganizationResponseDTO;
import com.greatbuild.clearcost.msvc.organizations.models.entities.Organization;
import com.greatbuild.clearcost.msvc.organizations.services.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controlador de Organizaciones con protección JWT stateless
 * 
 * Los roles vienen en el JWT firmado por msvc-users
 * No se consulta la base de datos para validar autenticación
 */
@RestController
@RequestMapping("/api/organizations")
@Tag(name = "Organizations", description = "Gestión de organizaciones y miembros")
@SecurityRequirement(name = "Bearer Authentication")
public class OrganizationController {

    private static final Logger log = LoggerFactory.getLogger(OrganizationController.class);
    private final OrganizationService service;

    public OrganizationController(OrganizationService service) {
        this.service = service;
    }

    /**
     * Lista todas las organizaciones
     * Cualquier usuario autenticado puede listar
     */
    @GetMapping
    @Operation(summary = "Listar todas las organizaciones", 
               description = "Obtiene la lista de todas las organizaciones registradas. No incluye la lista de members (solo el count).")
    public ResponseEntity<List<OrganizationResponseDTO>> listAll(Authentication authentication) {
        log.info("Usuario {} listando organizaciones", authentication.getName());
        List<OrganizationResponseDTO> organizations = service.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(organizations);
    }

    /**
     * Obtiene una organización por ID
     * Cualquier usuario autenticado puede ver
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener organización por ID", 
               description = "Consulta los detalles de una organización específica. No incluye la lista de members.")
    public ResponseEntity<OrganizationResponseDTO> getById(@PathVariable("id") Long id, Authentication authentication) {
        log.info("Usuario {} consultando organización {}", authentication.getName(), id);
        Optional<Organization> org = service.findById(id);
        return org.map(this::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Crea una nueva organización
     * Solo ROLE_WORKER puede crear organizaciones (se convierte en CONTRACTOR)
     * ROLE_CLIENT NO puede crear organizaciones
     */
    @PostMapping
    @PreAuthorize("hasRole('WORKER')")
    @Operation(summary = "Crear nueva organización", 
               description = "Crea una nueva organización. Solo usuarios con ROLE_WORKER pueden crear organizaciones. El usuario se convierte en CONTRACTOR (dueño) de la organización. La organización comienza sin miembros.")
    public ResponseEntity<?> create(@Valid @RequestBody CreateOrganizationDTO dto, Authentication authentication) {
        log.info("ROLE_WORKER {} creando organización (será CONTRACTOR)", 
                authentication.getName());
        try {
            // Convertir DTO a entidad
            Organization organization = new Organization();
            organization.setLegalName(dto.getLegalName());
            organization.setCommercialName(dto.getCommercialName());
            organization.setRuc(dto.getRuc());
            organization.setOwnerId(dto.getOwnerId());
            
            Organization newOrg = service.save(organization);
            log.info("Organización {} creada. Usuario {} es ahora CONTRACTOR de esta organización", 
                    newOrg.getId(), authentication.getName());
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(toResponseDTO(newOrg));
        } catch (IllegalArgumentException e) {
            log.warn("Error de validación al crear organización: {}", e.getMessage());
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            log.error("Error de servicio al crear organización: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    /**
     * Actualiza una organización
     * Solo ROLE_WORKER puede actualizar (debe ser el CONTRACTOR de la organización)
     * TODO: Verificar que el usuario sea el CONTRACTOR (ownerId) de la organización
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('WORKER')")
    @Operation(summary = "Actualizar organización", 
               description = "Actualiza la información de una organización. Solo el CONTRACTOR (owner) puede actualizar su organización.")
    public ResponseEntity<?> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody CreateOrganizationDTO dto,
            Authentication authentication) {
        log.info("Usuario {} (ROLE_WORKER) actualizando organización {}", authentication.getName(), id);
        Optional<Organization> existing = service.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        // TODO: Verificar que el usuario autenticado sea el ownerId de la organización
        // if (!existing.get().getOwnerId().equals(userIdFromAuthentication)) {
        //     return ResponseEntity.status(HttpStatus.FORBIDDEN)
        //         .body(Map.of("error", "Solo el CONTRACTOR puede actualizar la organización"));
        // }
        
        Organization organization = existing.get();
        organization.setLegalName(dto.getLegalName());
        organization.setCommercialName(dto.getCommercialName());
        organization.setRuc(dto.getRuc());
        organization.setOwnerId(dto.getOwnerId());
        
        Organization updated = service.save(organization);
        return ResponseEntity.ok(toResponseDTO(updated));
    }

    /**
     * Elimina una organización
     * Solo el CONTRACTOR (ownerId) puede eliminar su organización
     * TODO: Verificar que el usuario sea el CONTRACTOR (ownerId) de la organización
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('WORKER')")
    @Operation(summary = "Eliminar organización", 
               description = "Elimina una organización. Solo el CONTRACTOR (owner) puede eliminar su organización.")
    public ResponseEntity<?> delete(@PathVariable("id") Long id, Authentication authentication) {
        log.info("Usuario {} (ROLE_WORKER) eliminando organización {}", authentication.getName(), id);
        Optional<Organization> existing = service.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        // TODO: Verificar que el usuario autenticado sea el ownerId de la organización
        // if (!existing.get().getOwnerId().equals(userIdFromAuthentication)) {
        //     return ResponseEntity.status(HttpStatus.FORBIDDEN)
        //         .body(Map.of("error", "Solo el CONTRACTOR puede eliminar la organización"));
        // }
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Método helper para convertir Organization a OrganizationResponseDTO
     * Evita incluir la lista completa de members en la respuesta
     */
    private OrganizationResponseDTO toResponseDTO(Organization org) {
        return new OrganizationResponseDTO(
                org.getId(),
                org.getLegalName(),
                org.getCommercialName(),
                org.getRuc(),
                org.getOwnerId(),
                org.getCreatedAt(),
                org.getMembers() != null ? org.getMembers().size() : 0
        );
    }
}
