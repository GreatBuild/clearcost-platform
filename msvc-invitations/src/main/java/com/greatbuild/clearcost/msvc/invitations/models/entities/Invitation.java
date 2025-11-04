package com.greatbuild.clearcost.msvc.invitations.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "invitations")
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long organizationId; // ID de la organización (de msvc-organizations)

    @NotNull
    private Long inviterId; // ID del usuario que invita (de msvc-users)

    @NotNull
    private Long inviteeUserId; // ID del usuario invitado (de msvc-users)

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

    @Email
    private String inviteeEmail; // Email del invitado (para notificaciones)

    @NotNull
    @Enumerated(EnumType.STRING)
    private InvitationStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusDays(7); // ¡Tip! Las invitaciones deben expirar
        this.status = InvitationStatus.PENDING;
    }
}
