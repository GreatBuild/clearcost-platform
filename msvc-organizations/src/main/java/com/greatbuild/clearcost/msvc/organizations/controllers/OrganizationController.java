package com.greatbuild.clearcost.msvc.organizations.controllers;

import com.greatbuild.clearcost.msvc.organizations.models.dtos.CreateOrganizationDTO;
import com.greatbuild.clearcost.msvc.organizations.models.dtos.OrganizationResponseDTO;
import com.greatbuild.clearcost.msvc.organizations.models.entities.Organization;
import com.greatbuild.clearcost.msvc.organizations.models.entities.OrganizationMember;
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
        try {
            // Convertir DTO a entidad
            Organization organization = new Organization();
            organization.setLegalName(dto.getLegalName());
            organization.setCommercialName(dto.getCommercialName());
            organization.setRuc(dto.getRuc());
            organization.setOwnerId(dto.getOwnerId());
            
            Organization newOrg = service.save(organization);
            
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
     * Actualiza una organización (ACTUALIZACIÓN PARCIAL)
     * Solo ROLE_WORKER puede actualizar (debe ser el CONTRACTOR de la organización)
     * Los campos no enviados (null) no se actualizan
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('WORKER')")
    @Operation(summary = "Actualizar organización",
            description = "Actualiza la información de una organización (parcial). Solo actualiza los campos enviados. Solo el CONTRACTOR (owner) puede actualizar su organización.")
    public ResponseEntity<?> update(
            @PathVariable("id") Long id,
            @RequestBody com.greatbuild.clearcost.msvc.organizations.models.dtos.UpdateOrganizationDTO dto,
            Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            
            Optional<Organization> existing = service.findById(id);
            if (existing.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Organization organization = existing.get();
            
            // Verificar que el usuario autenticado sea el CONTRACTOR (ownerId) de la organización
            if (!organization.getOwnerId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(java.util.Map.of("error", "Solo el CONTRACTOR puede actualizar la organización"));
            }

            // ACTUALIZACIÓN PARCIAL: Solo actualizar campos no nulos
            if (dto.getLegalName() != null) {
                organization.setLegalName(dto.getLegalName());
            }
            if (dto.getCommercialName() != null) {
                organization.setCommercialName(dto.getCommercialName());
            }
            if (dto.getRuc() != null) {
                organization.setRuc(dto.getRuc());
            }

            Organization updated = service.save(organization);
            return ResponseEntity.ok(toResponseDTO(updated));
        } catch (NumberFormatException e) {
            log.error("Error al parsear userId del JWT: {}", authentication.getName());
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", "Token JWT inválido"));
        }
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
     * Agregar un miembro a una organización
     * Usado por el microservicio de invitaciones cuando un usuario acepta una invitación
     */
    @PostMapping("/{id}/members")
    @Operation(summary = "Agregar miembro a organización", 
               description = "Agrega un miembro a una organización. Usado internamente por el servicio de invitaciones.")
    public ResponseEntity<?> addMember(
            @PathVariable("id") Long id,
            @Valid @RequestBody com.greatbuild.clearcost.msvc.organizations.models.dtos.AddMemberDTO dto) {
        try {
            dto.setOrganizationId(id);
            OrganizationMember member = service.addMember(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(member);
        } catch (IllegalArgumentException e) {
            log.warn("Error al agregar miembro: {}", e.getMessage());
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            log.error("Error de servicio al agregar miembro: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtiene las organizaciones del usuario autenticado
     * Retorna organizaciones donde el usuario es CONTRACTOR (owner) o MEMBER con el rol del usuario
     */
    @GetMapping("/my-organizations")
    @PreAuthorize("hasRole('WORKER')")
    @Operation(summary = "Obtener mis organizaciones",
            description = "Obtiene todas las organizaciones donde el usuario autenticado es CONTRACTOR (owner) o MEMBER, incluyendo el rol del usuario en cada organización")
    public ResponseEntity<?> getMyOrganizations(Authentication authentication) {
        try {
            // Extraer el userId del JWT
            Long userId = Long.parseLong(authentication.getName());
            
            List<com.greatbuild.clearcost.msvc.organizations.models.dtos.UserOrganizationResponseDTO> organizations = 
                    service.getUserOrganizations(userId);
            
            return ResponseEntity.ok(organizations);
        } catch (NumberFormatException e) {
            log.error("Error al parsear userId del authentication: {}", authentication.getName());
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", "Usuario inválido en el token"));
        }
    }

    /**
     * Obtiene la lista de miembros de una organización con sus datos de usuario
     * Incluye al CONTRACTOR y todos los MEMBERS
     */
    @GetMapping("/{id}/members")
    @PreAuthorize("hasRole('WORKER')")
    @Operation(summary = "Obtener miembros de organización",
            description = "Obtiene la lista de miembros de una organización con sus datos (nombre, email, rol). Incluye al CONTRACTOR. El usuario solicitante debe ser CONTRACTOR o MEMBER de la organización.")
    public ResponseEntity<?> getOrganizationMembers(
            @PathVariable("id") Long id,
            Authentication authentication) {
        try {
            // Extraer userId del JWT
            Long userId = Long.parseLong(authentication.getName());
            
            // Verificar que la organización existe
            Optional<Organization> orgOpt = service.findById(id);
            if (orgOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Organization org = orgOpt.get();
            
            // Verificar que el usuario es CONTRACTOR (owner) o MEMBER de la organización
            boolean isContractor = org.getOwnerId().equals(userId);
            boolean isMember = org.getMembers().stream()
                    .anyMatch(m -> m.getUserId().equals(userId));
            
            if (!isContractor && !isMember) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(java.util.Map.of("error", "No tienes permisos para ver los miembros de esta organización"));
            }
            
            // Obtener miembros con datos de usuario (incluye CONTRACTOR + MEMBERS)
            List<com.greatbuild.clearcost.msvc.organizations.models.dtos.MemberResponseDTO> members = 
                    service.getMembersWithUserData(id);
            
            return ResponseEntity.ok(members);
        } catch (NumberFormatException e) {
            log.error("Error al parsear userId del JWT: {}", authentication.getName());
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", "Token JWT inválido"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    /**
     * Elimina un miembro de una organización
     * Solo el CONTRACTOR (owner) puede eliminar miembros
     */
    @DeleteMapping("/{orgId}/members/{memberId}")
    @PreAuthorize("hasRole('WORKER')")
    @Operation(summary = "Eliminar miembro de organización",
            description = "Elimina un miembro de una organización. Solo el CONTRACTOR (owner) puede eliminar miembros.")
    public ResponseEntity<?> removeMember(
            @PathVariable("orgId") Long orgId,
            @PathVariable("memberId") Long memberId,
            Authentication authentication) {
        try {
            // Extraer userId del JWT
            Long userId = Long.parseLong(authentication.getName());
            
            service.removeMember(orgId, memberId, userId);
            
            return ResponseEntity.noContent().build();
        } catch (NumberFormatException e) {
            log.error("Error al parsear userId del JWT: {}", authentication.getName());
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", "Token JWT inválido"));
        } catch (IllegalArgumentException e) {
            log.warn("Error al eliminar miembro: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", e.getMessage()));
        }
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
