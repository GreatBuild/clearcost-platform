package com.greatbuild.clearcost.msvc.projects.models.dtos;

import com.greatbuild.clearcost.msvc.projects.models.enums.ProjectRole;
import com.greatbuild.clearcost.msvc.projects.models.enums.Specialty;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para agregar un miembro al proyecto
 */
public class AddProjectMemberDTO {

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long userId;

    @NotNull(message = "El rol es obligatorio")
    private ProjectRole role;

    @NotNull(message = "La especialidad es obligatoria")
    private Specialty specialty;

    // Constructors
    public AddProjectMemberDTO() {
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public ProjectRole getRole() {
        return role;
    }

    public void setRole(ProjectRole role) {
        this.role = role;
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public void setSpecialty(Specialty specialty) {
        this.specialty = specialty;
    }
}
