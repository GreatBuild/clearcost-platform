package com.greatbuild.clearcost.msvc.invitations.services;

import com.greatbuild.clearcost.msvc.invitations.models.dtos.CreateInvitationDTO;
import com.greatbuild.clearcost.msvc.invitations.models.dtos.InvitationResponseDTO;
import com.greatbuild.clearcost.msvc.invitations.models.entities.Invitation;

import java.util.List;

public interface InvitationService {

    /**
     * Crea una nueva invitación (usado por el controlador REST).
     */
    InvitationResponseDTO createInvitation(CreateInvitationDTO dto, Long inviterId);

    /**
     * Crea una invitación desde un evento de RabbitMQ.
     */
    void createInvitation(Invitation invitation);

    /**
     * Acepta una invitación.
     */
    InvitationResponseDTO acceptInvitation(Long invitationId, Long userId);

    /**
     * Rechaza una invitación.
     */
    InvitationResponseDTO rejectInvitation(Long invitationId, Long userId);

    /**
     * Obtiene todas las invitaciones de un usuario.
     */
    List<InvitationResponseDTO> getUserInvitations(Long userId);

    /**
     * Obtiene todas las invitaciones pendientes de un usuario.
     */
    List<InvitationResponseDTO> getPendingInvitations(Long userId);
}
