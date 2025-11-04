package com.greatbuild.clearcost.msvc.invitations.models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public class CreateInvitationDTO {

    @NotNull(message = "El ID de la organización es obligatorio")
    private Long organizationId;

    @NotNull(message = "El ID del usuario invitado es obligatorio")
    private Long inviteeUserId;

    @Email(message = "El email debe ser válido")
    private String inviteeEmail;

    // Getters y Setters
    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
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
}
