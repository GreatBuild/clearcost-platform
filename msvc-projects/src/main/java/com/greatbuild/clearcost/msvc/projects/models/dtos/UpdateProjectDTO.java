package com.greatbuild.clearcost.msvc.projects.models.dtos;

import com.greatbuild.clearcost.msvc.projects.models.enums.ProjectStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO para actualización parcial de un proyecto.
 * Todos los campos son opcionales; solo se modifican los enviados.
 */
public class UpdateProjectDTO {

    @Size(min = 1, message = "El nombre del proyecto no puede estar vacío")
    private String projectName;

    private String description;

    private LocalDate endDate;

    @Email(message = "El correo de la entidad contratante debe ser válido")
    private String contractingEntityEmail;

    private ProjectStatus status;

    public UpdateProjectDTO() {
    }

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

    public String getContractingEntityEmail() {
        return contractingEntityEmail;
    }

    public void setContractingEntityEmail(String contractingEntityEmail) {
        this.contractingEntityEmail = contractingEntityEmail;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }
}
