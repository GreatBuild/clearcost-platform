package com.greatbuild.clearcost.msvc.invitations.models.dtos;

import java.time.LocalDateTime;

/**
 * DTO para solicitar validación de invitación de forma asíncrona
 * Se envía a RabbitMQ para que otros servicios validen la invitación
 */
public class InvitationValidationRequestDTO {

    private Long invitationId;
    private Long organizationId;
    private Long inviterId;
    private Long inviteeUserId;
    private String inviteeEmail;
    private LocalDateTime requestedAt;
    private String reason; // Motivo del rechazo si la validación falla

    public InvitationValidationRequestDTO() {
        this.requestedAt = LocalDateTime.now();
    }

    public InvitationValidationRequestDTO(Long invitationId, Long organizationId, 
            Long inviterId, Long inviteeUserId, String inviteeEmail) {
        this.invitationId = invitationId;
        this.organizationId = organizationId;
        this.inviterId = inviterId;
        this.inviteeUserId = inviteeUserId;
        this.inviteeEmail = inviteeEmail;
        this.requestedAt = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getInvitationId() {
        return invitationId;
    }

    public void setInvitationId(Long invitationId) {
        this.invitationId = invitationId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getInviterId() {
        return inviterId;
    }

    public void setInviterId(Long inviterId) {
        this.inviterId = inviterId;
    }

    public Long getInviteeUserId() {
        return inviteeUserId;
    }

    public void setInviteeUserId(Long inviteeUserId) {
        this.inviteeUserId = inviteeUserId;
    }

    public String getInviteeEmail() {
        return inviteeEmail;
    }

    public void setInviteeEmail(String inviteeEmail) {
        this.inviteeEmail = inviteeEmail;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
