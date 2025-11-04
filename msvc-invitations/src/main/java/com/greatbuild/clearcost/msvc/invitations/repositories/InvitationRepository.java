package com.greatbuild.clearcost.msvc.invitations.repositories;

import com.greatbuild.clearcost.msvc.invitations.models.entities.Invitation;
import com.greatbuild.clearcost.msvc.invitations.models.entities.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    // Para que un usuario pueda ver todas sus invitaciones pendientes
    List<Invitation> findByInviteeUserIdAndStatus(Long inviteeUserId, InvitationStatus status);

    // Verificar si ya existe una invitación PENDIENTE para este usuario en esta organización
    Optional<Invitation> findByOrganizationIdAndInviteeUserIdAndStatus(
            Long organizationId, Long inviteeUserId, InvitationStatus status);

    // Obtener todas las invitaciones de un usuario (cualquier estado)
    List<Invitation> findByInviteeUserId(Long inviteeUserId);

    // Obtener todas las invitaciones de una organización
    List<Invitation> findByOrganizationId(Long organizationId);
}
