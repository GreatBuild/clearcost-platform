package com.greatbuild.clearcost.msvc.organizations.events;

import com.greatbuild.clearcost.msvc.organizations.clients.UserFeignClient;
import com.greatbuild.clearcost.msvc.organizations.models.entities.Organization;
import com.greatbuild.clearcost.msvc.organizations.services.OrganizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Listener que valida invitaciones desde msvc-invitations
 * Verifica que la organización existe y que el invitador es el owner
 */
@Component
public class InvitationValidationListener {

    private static final Logger log = LoggerFactory.getLogger(InvitationValidationListener.class);
    
    private final OrganizationService organizationService;
    private final UserFeignClient userClient;
    private final RabbitTemplate rabbitTemplate;

    public InvitationValidationListener(OrganizationService organizationService,
                                        UserFeignClient userClient,
                                        RabbitTemplate rabbitTemplate) {
        this.organizationService = organizationService;
        this.userClient = userClient;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "invitation.validation.request.queue")
    public void validateInvitation(InvitationValidationRequestDTO request) {
        try {
            // 1. Verificar que la organización existe
            Optional<Organization> orgOpt = organizationService.findById(request.getOrganizationId());
            if (orgOpt.isEmpty()) {
                request.setReason("La organización no existe");
                publishValidationFailed(request);
                return;
            }

            Organization org = orgOpt.get();

            // 2. Verificar que el invitador es el owner de la organización
            if (!org.getOwnerId().equals(request.getInviterId())) {
                request.setReason("Solo el creador de la organización puede enviar invitaciones");
                publishValidationFailed(request);
                return;
            }

            // 3. Verificar que el usuario invitado existe
            try {
                userClient.getUserById(request.getInviteeUserId());
            } catch (Exception e) {
                request.setReason("El usuario invitado no existe");
                publishValidationFailed(request);
                return;
            }

            // 4. Verificar que el usuario no es ya miembro
            boolean alreadyMember = org.getMembers().stream()
                    .anyMatch(m -> m.getUserId().equals(request.getInviteeUserId()));
            
            if (alreadyMember) {
                request.setReason("El usuario ya es miembro de la organización");
                publishValidationFailed(request);
                return;
            }

            // Todas las validaciones pasaron
            publishValidationSuccess(request);

        } catch (Exception e) {
            log.error("Error al validar invitación {}: {}", request.getInvitationId(), e.getMessage());
            request.setReason("Error interno durante la validación");
            publishValidationFailed(request);
        }
    }

    private void publishValidationSuccess(InvitationValidationRequestDTO request) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    RabbitMQConfig.ROUTING_KEY_SUCCESS,
                    request
            );
        } catch (Exception e) {
            log.error("Error al publicar validación exitosa: {}", e.getMessage());
        }
    }

    private void publishValidationFailed(InvitationValidationRequestDTO request) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    RabbitMQConfig.ROUTING_KEY_FAILED,
                    request
            );
        } catch (Exception e) {
            log.error("Error al publicar validación fallida: {}", e.getMessage());
        }
    }
}

/**
 * DTO para validación de invitaciones (debe coincidir con msvc-invitations)
 */
class InvitationValidationRequestDTO {
    private Long invitationId;
    private Long organizationId;
    private Long inviterId;
    private Long inviteeUserId;
    private String inviteeEmail;
    private LocalDateTime requestedAt;
    private String reason;

    // Getters y Setters
    public Long getInvitationId() { return invitationId; }
    public void setInvitationId(Long invitationId) { this.invitationId = invitationId; }
    
    public Long getOrganizationId() { return organizationId; }
    public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }
    
    public Long getInviterId() { return inviterId; }
    public void setInviterId(Long inviterId) { this.inviterId = inviterId; }
    
    public Long getInviteeUserId() { return inviteeUserId; }
    public void setInviteeUserId(Long inviteeUserId) { this.inviteeUserId = inviteeUserId; }
    
    public String getInviteeEmail() { return inviteeEmail; }
    public void setInviteeEmail(String inviteeEmail) { this.inviteeEmail = inviteeEmail; }
    
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
