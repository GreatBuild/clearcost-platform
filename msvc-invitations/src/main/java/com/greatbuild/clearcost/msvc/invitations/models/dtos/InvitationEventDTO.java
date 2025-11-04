package com.greatbuild.clearcost.msvc.invitations.models.dtos;

import com.greatbuild.clearcost.msvc.invitations.models.entities.InvitationStatus;
import java.time.LocalDateTime;

/**
 * DTO para eventos de invitación publicados en RabbitMQ
 * No incluye entidades JPA para evitar problemas de serialización
 */
public class InvitationEventDTO {

    private Long id;
    private Long organizationId;
    private Long inviterId;
    private Long inviteeUserId;
    private String inviteeEmail;
    private InvitationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime eventTimestamp;

    // Constructor vacío
    public InvitationEventDTO() {
        this.eventTimestamp = LocalDateTime.now();
    }

    // Constructor con parámetros
    public InvitationEventDTO(Long id, Long organizationId, Long inviterId, 
                             Long inviteeUserId, String inviteeEmail, 
                             InvitationStatus status, LocalDateTime createdAt, 
                             LocalDateTime expiresAt) {
        this.id = id;
        this.organizationId = organizationId;
        this.inviterId = inviterId;
        this.inviteeUserId = inviteeUserId;
        this.inviteeEmail = inviteeEmail;
        this.status = status;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.eventTimestamp = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public InvitationStatus getStatus() {
        return status;
    }

    public void setStatus(InvitationStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(LocalDateTime eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }
}
