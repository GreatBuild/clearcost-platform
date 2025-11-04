package com.greatbuild.clearcost.msvc.invitations.services;

import com.greatbuild.clearcost.msvc.invitations.models.entities.Invitation;

public interface InvitationService {

    /**
     * Crea una nueva invitación.
     * Este método es llamado por el listener de RabbitMQ.
     */
    void createInvitation(Invitation invitation);

    // (Más adelante añadiremos acceptInvitation y rejectInvitation)
}
