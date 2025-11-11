package com.greatbuild.clearcost.msvc.projects.models.dtos;

import com.greatbuild.clearcost.msvc.projects.models.enums.ProjectStatus;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para actualizar el status de un proyecto
 */
public class UpdateProjectStatusDTO {

    @NotNull(message = "El status es obligatorio")
    private ProjectStatus status;

    // Constructors
    public UpdateProjectStatusDTO() {
    }

    // Getters and Setters
    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }
}
