package com.greatbuild.clearcost.msvc.projects.controllers;

import com.greatbuild.clearcost.msvc.projects.models.dtos.ProjectMemberResponseDTO;
import com.greatbuild.clearcost.msvc.projects.models.dtos.ProjectResponseDTO;
import com.greatbuild.clearcost.msvc.projects.models.entities.Project;
import com.greatbuild.clearcost.msvc.projects.services.ProjectService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controlador INTERNO para comunicación entre microservicios
 * Sin autenticación JWT (endpoints internos)
 */
@RestController
@RequestMapping("/api/projects/internal")
@Hidden // Oculto de Swagger
public class ProjectInternalController {

    private final ProjectService service;

    public ProjectInternalController(ProjectService service) {
        this.service = service;
    }

    /**
     * Obtiene un proyecto por ID (sin autenticación)
     * Usado por otros microservicios
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> getProjectById(@PathVariable("id") Long id) {
        Optional<Project> project = service.findById(id);
        return project.map(this::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Obtiene los miembros de un proyecto (sin autenticación)
     * Usado por otros microservicios
     */
    @GetMapping("/{id}/members")
    public ResponseEntity<List<ProjectMemberResponseDTO>> getProjectMembers(@PathVariable("id") Long id) {
        try {
            List<ProjectMemberResponseDTO> members = service.getProjectMembers(id);
            return ResponseEntity.ok(members);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

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
