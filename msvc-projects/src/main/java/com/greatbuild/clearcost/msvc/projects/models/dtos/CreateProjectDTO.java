package com.greatbuild.clearcost.msvc.projects.models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * DTO para crear un proyecto
 */
public class CreateProjectDTO {

    @NotBlank(message = "El nombre del proyecto es obligatorio")
    private String projectName;

    private String description;

    @NotNull(message = "La fecha de finalización es obligatoria")
    private LocalDate endDate;

    @NotNull(message = "El ID de la organización es obligatorio")
    private Long organizationId;

    @NotEmpty(message = "El correo de la entidad contratante es obligatorio")
    @Email(message = "El correo de la entidad contratante debe ser válido")
    private String contractingEntityEmail;

    // Constructors
    public CreateProjectDTO() {
    }

    // Getters and Setters
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getContractingEntityEmail() {
        return contractingEntityEmail;
    }

    public void setContractingEntityEmail(String contractingEntityEmail) {
        this.contractingEntityEmail = contractingEntityEmail;
    }
}
