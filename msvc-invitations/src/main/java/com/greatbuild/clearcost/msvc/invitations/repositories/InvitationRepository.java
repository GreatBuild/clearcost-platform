package com.greatbuild.clearcost.msvc.invitations.repositories;

import com.greatbuild.clearcost.msvc.invitations.models.entities.Invitation;
import com.greatbuild.clearcost.msvc.invitations.models.entities.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    // Para que un usuario pueda ver todas sus invitaciones pendientes
    List<Invitation> findByInviteeUserIdAndStatus(Long inviteeUserId, InvitationStatus status);
}
