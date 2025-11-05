package com.greatbuild.clearcost.msvc.invitations.events;

import com.greatbuild.clearcost.msvc.invitations.models.dtos.InvitationValidationRequestDTO;
import com.greatbuild.clearcost.msvc.invitations.models.entities.Invitation;
import com.greatbuild.clearcost.msvc.invitations.models.entities.InvitationStatus;
import com.greatbuild.clearcost.msvc.invitations.repositories.InvitationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Listener para procesar respuestas de validación asíncrona
 * Recibe eventos de validación exitosa o fallida y actualiza el estado de la invitación
 */
@Component
public class ValidationResponseListener {

    private static final Logger log = LoggerFactory.getLogger(ValidationResponseListener.class);
    private final InvitationRepository repository;

    public ValidationResponseListener(InvitationRepository repository) {
        this.repository = repository;
    }

    /**
     * Procesa validaciones exitosas
     * Cambia el estado de PENDING_PROCESSING a PENDING
     */
    @RabbitListener(queues = RabbitMQConfig.VALIDATION_SUCCESS_QUEUE)
    @Transactional
    public void handleValidationSuccess(InvitationValidationRequestDTO validation) {
        try {
            Invitation invitation = repository.findById(validation.getInvitationId())
                    .orElseThrow(() -> new RuntimeException("Invitación no encontrada"));

            if (invitation.getStatus() == InvitationStatus.PENDING_PROCESSING) {
                invitation.setStatus(InvitationStatus.PENDING);
                repository.save(invitation);
            }
        } catch (Exception e) {
            log.error("Error al procesar validación exitosa: {}", e.getMessage());
        }
    }

    /**
     * Procesa validaciones fallidas
     * Cambia el estado de PENDING_PROCESSING a REJECTED
     */
    @RabbitListener(queues = RabbitMQConfig.VALIDATION_FAILED_QUEUE)
    @Transactional
    public void handleValidationFailed(InvitationValidationRequestDTO validation) {
        try {
            Invitation invitation = repository.findById(validation.getInvitationId())
                    .orElseThrow(() -> new RuntimeException("Invitación no encontrada"));

            if (invitation.getStatus() == InvitationStatus.PENDING_PROCESSING) {
                invitation.setStatus(InvitationStatus.REJECTED);
                repository.save(invitation);
                log.warn("Invitación {} rechazada por validación fallida: {}", 
                    invitation.getId(), validation.getReason());
            }
        } catch (Exception e) {
            log.error("Error al procesar validación fallida: {}", e.getMessage());
        }
    }
}
