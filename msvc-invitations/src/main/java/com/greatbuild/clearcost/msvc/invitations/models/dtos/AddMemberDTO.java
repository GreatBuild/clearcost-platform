package com.greatbuild.clearcost.msvc.invitations.models.dtos;

import jakarta.validation.constraints.NotNull;

public class AddMemberDTO {

    @NotNull
    private Long userId;

    @NotNull
    private Long organizationId;

    private String role; // "WORKER" por defecto cuando acepta invitación

    // Getters y Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
