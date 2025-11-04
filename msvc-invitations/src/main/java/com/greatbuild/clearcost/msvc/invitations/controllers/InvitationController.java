package com.greatbuild.clearcost.msvc.invitations.controllers;

import com.greatbuild.clearcost.msvc.invitations.models.dtos.CreateInvitationDTO;
import com.greatbuild.clearcost.msvc.invitations.models.dtos.InvitationResponseDTO;
import com.greatbuild.clearcost.msvc.invitations.services.InvitationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invitations")
@SecurityRequirement(name = "Bearer Authentication")
public class InvitationController {

    private final InvitationService invitationService;

    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    /**
     * Crear una nueva invitación (solo para usuarios CONTRACTOR que sean creadores de la organización)
     */
    @PostMapping
    public ResponseEntity<?> createInvitation(
            @Valid @RequestBody CreateInvitationDTO dto,
            Authentication authentication) {
        try {
            // Obtener el ID del usuario autenticado desde el JWT
            Long inviterId = Long.parseLong(authentication.getName());
            System.out.println("Inviter ID: " + inviterId);
            
            InvitationResponseDTO response = invitationService.createInvitation(dto, inviterId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Aceptar una invitación
     */
    @PostMapping("/{id}/accept")
    public ResponseEntity<?> acceptInvitation(
            @PathVariable("id") Long id,
            Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            InvitationResponseDTO response = invitationService.acceptInvitation(id, userId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Rechazar una invitación
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectInvitation(
            @PathVariable("id") Long id,
            Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            InvitationResponseDTO response = invitationService.rejectInvitation(id, userId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Obtener todas las invitaciones del usuario autenticado
     */
    @GetMapping("/my-invitations")
    public ResponseEntity<List<InvitationResponseDTO>> getMyInvitations(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        List<InvitationResponseDTO> invitations = invitationService.getUserInvitations(userId);
        return ResponseEntity.ok(invitations);
    }

    /**
     * Obtener todas las invitaciones pendientes del usuario autenticado
     */
    @GetMapping("/pending")
    public ResponseEntity<List<InvitationResponseDTO>> getPendingInvitations(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        List<InvitationResponseDTO> invitations = invitationService.getPendingInvitations(userId);
        return ResponseEntity.ok(invitations);
    }
}
