package com.greatbuild.clearcost.msvc.organizations.controllers;

import com.greatbuild.clearcost.msvc.organizations.models.dtos.MemberResponseDTO;
import com.greatbuild.clearcost.msvc.organizations.models.dtos.OrganizationResponseDTO;
import com.greatbuild.clearcost.msvc.organizations.models.entities.Organization;
import com.greatbuild.clearcost.msvc.organizations.services.OrganizationService;
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
@RequestMapping("/api/organizations/internal")
@Hidden // Oculto de Swagger
public class OrganizationInternalController {

    private final OrganizationService service;

    public OrganizationInternalController(OrganizationService service) {
        this.service = service;
    }

    /**
     * Obtiene una organización por ID (sin autenticación)
     * Usado por otros microservicios
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrganizationResponseDTO> getOrganizationById(@PathVariable("id") Long id) {
        Optional<Organization> org = service.findById(id);
        return org.map(this::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Obtiene los miembros de una organización (sin autenticación)
     * Usado por otros microservicios
     */
    @GetMapping("/{id}/members")
    public ResponseEntity<List<MemberResponseDTO>> getOrganizationMembers(@PathVariable("id") Long id) {
        try {
            List<MemberResponseDTO> members = service.getMembersWithUserData(id);
            return ResponseEntity.ok(members);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

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
