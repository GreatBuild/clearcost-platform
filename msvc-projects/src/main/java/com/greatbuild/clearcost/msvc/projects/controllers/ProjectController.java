package com.greatbuild.clearcost.msvc.projects.controllers;

import com.greatbuild.clearcost.msvc.projects.models.dtos.*;
import com.greatbuild.clearcost.msvc.projects.models.entities.Project;
import com.greatbuild.clearcost.msvc.projects.services.ProjectService;
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
 * Controlador de Proyectos con protección JWT stateless
 */
@RestController
@RequestMapping("/api/projects")
@Tag(name = "Projects", description = "Gestión de proyectos de construcción")
@SecurityRequirement(name = "Bearer Authentication")
public class ProjectController {

    private static final Logger log = LoggerFactory.getLogger(ProjectController.class);
    private final ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
    }

    /**
     * Crea un nuevo proyecto
     * Solo ROLE_WORKER (que es CONTRACTOR de la organización) puede crear proyectos
     */
    @PostMapping
    @PreAuthorize("hasRole('WORKER')")
    @Operation(summary = "Crear proyecto", 
               description = "Crea un nuevo proyecto. Solo el CONTRACTOR de la organización puede crear proyectos. El creador se agrega automáticamente como COORDINATOR.")
    public ResponseEntity<?> createProject(
            @Valid @RequestBody CreateProjectDTO dto,
            Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            
            Project project = service.createProject(dto, userId);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(toResponseDTO(project));
        } catch (NumberFormatException e) {
            log.error("Error al parsear userId: {}", authentication.getName());
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", "Usuario inválido"));
        } catch (IllegalArgumentException e) {
            log.warn("Error de validación al crear proyecto: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            log.error("Error de servicio al crear proyecto: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtiene un proyecto por ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('WORKER') or hasRole('CLIENT')")
    @Operation(summary = "Obtener proyecto por ID",
               description = "Consulta los detalles de un proyecto específico")
    public ResponseEntity<?> getById(@PathVariable("id") Long id) {
        Optional<Project> project = service.findById(id);
        return project.map(this::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Actualiza un proyecto
     * Solo COORDINATOR puede actualizar
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('WORKER')")
    @Operation(summary = "Actualizar proyecto",
               description = "Actualiza la información de un proyecto. Solo COORDINATOR puede actualizar.")
    public ResponseEntity<?> updateProject(
            @PathVariable("id") Long id,
            @Valid @RequestBody CreateProjectDTO dto,
            Authentication authentication) {
        try {
            Project project = service.updateProject(id, dto);
            return ResponseEntity.ok(toResponseDTO(project));
        } catch (IllegalArgumentException e) {
            log.warn("Error al actualizar proyecto: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    /**
     * Elimina un proyecto
     * Solo COORDINATOR puede eliminar
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('WORKER')")
    @Operation(summary = "Eliminar proyecto",
               description = "Elimina un proyecto. Solo COORDINATOR puede eliminar.")
    public ResponseEntity<?> deleteProject(@PathVariable("id") Long id) {
        try {
            service.deleteProject(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.warn("Error al eliminar proyecto: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    /**
     * Actualiza el status de un proyecto
     * Solo COORDINATOR puede cambiar el status
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('WORKER')")
    @Operation(summary = "Actualizar status del proyecto",
               description = "Cambia el estado del proyecto. Solo COORDINATOR puede cambiar el status.")
    public ResponseEntity<?> updateStatus(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateProjectStatusDTO dto) {
        try {
            Project project = service.updateStatus(id, dto.getStatus());
            return ResponseEntity.ok(toResponseDTO(project));
        } catch (IllegalArgumentException e) {
            log.warn("Error al actualizar status: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtiene proyectos de una organización filtrados por rol del usuario
     */
    @GetMapping("/organization/{orgId}/my-projects")
    @PreAuthorize("hasRole('WORKER')")
    @Operation(summary = "Mis proyectos de una organización",
               description = "Obtiene los proyectos de una organización. Si es CONTRACTOR, obtiene todos. Si es MEMBER, solo los proyectos donde es miembro.")
    public ResponseEntity<?> getMyProjectsByOrganization(
            @PathVariable("orgId") Long orgId,
            Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            
            List<ProjectResponseDTO> projects = service.getMyProjectsByOrganization(orgId, userId).stream()
                    .map(this::toResponseDTO)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(projects);
        } catch (NumberFormatException e) {
            log.error("Error al parsear userId: {}", authentication.getName());
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", "Usuario inválido"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(java.util.Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtiene proyectos donde el usuario es contractingEntity (cliente)
     */
    @GetMapping("/as-client")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Proyectos como cliente",
               description = "Obtiene todos los proyectos donde el usuario autenticado es el contractingEntity (cliente).")
    public ResponseEntity<?> getProjectsAsClient(Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            
            List<ProjectResponseDTO> projects = service.getProjectsAsClient(userId).stream()
                    .map(this::toResponseDTO)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(projects);
        } catch (NumberFormatException e) {
            log.error("Error al parsear userId: {}", authentication.getName());
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", "Usuario inválido"));
        }
    }

    /**
     * Obtiene los miembros de un proyecto con sus datos
     */
    @GetMapping("/{id}/members")
    @PreAuthorize("hasRole('WORKER') or hasRole('CLIENT')")
    @Operation(summary = "Obtener miembros del proyecto",
               description = "Obtiene la lista de miembros del proyecto con sus datos completos (nombre, email, rol, especialidad).")
    public ResponseEntity<?> getProjectMembers(@PathVariable("id") Long id) {
        try {
            List<ProjectMemberResponseDTO> members = service.getProjectMembers(id);
            return ResponseEntity.ok(members);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    /**
     * Agrega un miembro al proyecto
     * Solo COORDINATOR puede agregar miembros
     */
    @PostMapping("/{id}/members")
    @PreAuthorize("hasRole('WORKER')")
    @Operation(summary = "Agregar miembro al proyecto",
               description = "Agrega un miembro al proyecto. Solo COORDINATOR puede agregar miembros. El miembro debe pertenecer a la organización.")
    public ResponseEntity<?> addMember(
            @PathVariable("id") Long id,
            @Valid @RequestBody AddProjectMemberDTO dto) {
        try {
            service.addMember(id, dto);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            log.warn("Error al agregar miembro: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            log.error("Error de servicio al agregar miembro: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    /**
     * Elimina un miembro del proyecto
     * Solo COORDINATOR puede eliminar miembros
     */
    @DeleteMapping("/{projectId}/members/{memberId}")
    @PreAuthorize("hasRole('WORKER')")
    @Operation(summary = "Eliminar miembro del proyecto",
               description = "Elimina un miembro del proyecto. Solo COORDINATOR puede eliminar miembros.")
    public ResponseEntity<?> removeMember(
            @PathVariable("projectId") Long projectId,
            @PathVariable("memberId") Long memberId) {
        try {
            service.removeMember(projectId, memberId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.warn("Error al eliminar miembro: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    /**
     * Método helper para convertir Project a ProjectResponseDTO
     */
    private ProjectResponseDTO toResponseDTO(Project project) {
        return new ProjectResponseDTO(
                project.getId(),
                project.getProjectName(),
                project.getDescription(),
                project.getStartDate(),
                project.getEndDate(),
                project.getOrganizationId(),
                project.getContractingEntityId(),
                project.getStatus(),
                project.getCreatedAt(),
                project.getMembers() != null ? project.getMembers().size() : 0
        );
    }
}
