package com.greatbuild.clearcost.msvc.msvcchange.controllers;

import com.greatbuild.clearcost.msvc.msvcchange.models.dtos.ChangeProcessResponseDTO;
import com.greatbuild.clearcost.msvc.msvcchange.models.dtos.CreateChangeProcessDTO;
import com.greatbuild.clearcost.msvc.msvcchange.models.dtos.UpdateChangeProcessDTO;
import com.greatbuild.clearcost.msvc.msvcchange.models.entities.ChangeProcess;
import com.greatbuild.clearcost.msvc.msvcchange.models.enums.ChangeProcessStatus;
import com.greatbuild.clearcost.msvc.msvcchange.services.ChangeProcessService;
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
import java.util.stream.Collectors;

/**
 * Controlador de solicitudes de cambio con protección JWT stateless
 */
@RestController
@RequestMapping("/api/change-process")
@Tag(name = "Change Process", description = "Gestión de solicitudes de cambio en proyectos")
@SecurityRequirement(name = "Bearer Authentication")
public class ChangeProcessController {

    private static final Logger log = LoggerFactory.getLogger(ChangeProcessController.class);
    private final ChangeProcessService service;

    public ChangeProcessController(ChangeProcessService service) {
        this.service = service;
    }

    /**
     * Crea una nueva solicitud de cambio
     * Solo ROLE_CLIENT (contractingEntity) puede crear solicitudes
     */
    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Crear solicitud de cambio",
            description = "Crea una nueva solicitud de cambio. Solo el contractingEntity del proyecto puede crear solicitudes. El proyecto no debe estar en status APPROVED.")
    public ResponseEntity<?> createChangeProcess(
            @Valid @RequestBody CreateChangeProcessDTO dto,
            Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());

            ChangeProcess changeProcess = service.createChangeProcess(dto, userId);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(toResponseDTO(changeProcess));
        } catch (NumberFormatException e) {
            log.error("Error al parsear userId: {}", authentication.getName());
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", "Usuario inválido"));
        } catch (IllegalArgumentException e) {
            log.warn("Error de validación al crear solicitud: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            log.error("Error de servicio al crear solicitud: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    /**
     * Actualiza (responde) una solicitud de cambio
     * Solo ROLE_WORKER (COORDINATOR del proyecto) puede responder
     */
    @PatchMapping("/{changeProcessId}")
    @PreAuthorize("hasRole('WORKER')")
    @Operation(summary = "Responder solicitud de cambio",
            description = "Actualiza una solicitud de cambio con una respuesta y cambia el status a APPROVED o REJECTED. Solo el COORDINATOR del proyecto puede responder. La solicitud debe estar en status PENDING.")
    public ResponseEntity<?> updateChangeProcess(
            @PathVariable("changeProcessId") Long changeProcessId,
            @Valid @RequestBody UpdateChangeProcessDTO dto,
            Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());

            ChangeProcess changeProcess = service.updateChangeProcess(changeProcessId, dto, userId);

            return ResponseEntity.ok(toResponseDTO(changeProcess));
        } catch (NumberFormatException e) {
            log.error("Error al parsear userId: {}", authentication.getName());
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", "Usuario inválido"));
        } catch (IllegalArgumentException e) {
            log.warn("Error al actualizar solicitud: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            log.error("Error de servicio al actualizar solicitud: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtiene las solicitudes de cambio de un proyecto
     * Pueden consultar: CONTRACTOR, COORDINATOR o contractingEntity
     */
    @GetMapping("/by-project-id/{projectId}")
    @PreAuthorize("hasRole('WORKER') or hasRole('CLIENT')")
    @Operation(summary = "Obtener solicitudes de cambio por proyecto",
            description = "Obtiene todas las solicitudes de cambio de un proyecto. Pueden consultar: CONTRACTOR de la org, COORDINATOR del proyecto o contractingEntity del proyecto.")
    public ResponseEntity<?> getChangeProcessesByProject(
            @PathVariable("projectId") Long projectId,
            Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());

            List<ChangeProcessResponseDTO> changeProcesses = service.getChangeProcessesByProject(projectId, userId)
                    .stream()
                    .map(this::toResponseDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(changeProcesses);
        } catch (NumberFormatException e) {
            log.error("Error al parsear userId: {}", authentication.getName());
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", "Usuario inválido"));
        } catch (IllegalArgumentException e) {
            log.warn("Error al obtener solicitudes: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(java.util.Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            log.error("Error de servicio al obtener solicitudes: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    /**
     * Convierte ChangeProcess a ChangeProcessResponseDTO
     */
    private ChangeProcessResponseDTO toResponseDTO(ChangeProcess cp) {
        String statusName = ChangeProcessStatus.fromId(cp.getStatusId()).getName();

        return new ChangeProcessResponseDTO(
                cp.getId(),
                cp.getOrigin(),
                statusName,
                cp.getJustification(),
                cp.getResponse(),
                cp.getProjectId()
        );
    }
}
