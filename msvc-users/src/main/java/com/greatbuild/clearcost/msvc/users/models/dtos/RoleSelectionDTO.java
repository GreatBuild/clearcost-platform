package com.greatbuild.clearcost.msvc.users.models.dtos;

import jakarta.validation.constraints.NotEmpty;

public class RoleSelectionDTO {
    @NotEmpty
    private String roleName; // "ROLE_CLIENT" o "ROLE_WORKER"

    // Getters y Setters (¡El setter es usado por Jackson!)
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
}
