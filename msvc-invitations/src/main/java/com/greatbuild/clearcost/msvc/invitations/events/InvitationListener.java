package com.greatbuild.clearcost.msvc.invitations.events;

import com.greatbuild.clearcost.msvc.invitations.models.entities.Invitation;
import com.greatbuild.clearcost.msvc.invitations.services.InvitationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class InvitationListener {

    private static final Logger log = LoggerFactory.getLogger(InvitationListener.class);
    private final InvitationService invitationService;

    public InvitationListener(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    /**
     * ¡¡ESTE ES EL OÍDO!!
     * Escucha cualquier mensaje que llegue a la cola definida en RabbitMQConfig.
     */
    @RabbitListener(queues = RabbitMQConfig.INVITATION_QUEUE)
    public void handleInvitation(Invitation invitation) {
        try {
            invitationService.createInvitation(invitation);
        } catch (Exception e) {
            log.error("Error al procesar la invitación desde RabbitMQ: {}", e.getMessage());
        }
    }
}
