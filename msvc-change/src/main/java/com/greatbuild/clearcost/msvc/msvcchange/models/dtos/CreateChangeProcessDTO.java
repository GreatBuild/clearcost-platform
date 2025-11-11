package com.greatbuild.clearcost.msvc.msvcchange.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para crear una solicitud de cambio
 */
public class CreateChangeProcessDTO {

    @NotNull(message = "El projectId es obligatorio")
    private Long projectId;

    @NotBlank(message = "La justification es obligatoria")
    private String justification;

    // Constructors
    public CreateChangeProcessDTO() {
    }

    // Getters and Setters
    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }
}
