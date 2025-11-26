package com.greatbuild.clearcost.msvc.invitations.models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class CreateInvitationDTO {

    @NotNull(message = "El ID de la organización es obligatorio")
    private Long organizationId;

    @NotEmpty(message = "El email del usuario invitado es obligatorio")
    @Email(message = "El email debe ser válido")
    private String inviteeEmail;

    // Campo opcional - se buscará automáticamente por email si no se proporciona
    private Long inviteeUserId;

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
