package com.greatbuild.clearcost.msvc.organizations.models.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para crear una nueva organización
 * No incluye members (la organización comienza sin miembros)
 */
public class CreateOrganizationDTO {

    @NotEmpty(message = "El nombre legal es obligatorio")
    private String legalName;

    @NotEmpty(message = "El nombre comercial es obligatorio")
    private String commercialName;

    @NotEmpty(message = "El RUC es obligatorio")
    private String ruc;

    @NotNull(message = "El ID del dueño (ownerId) es obligatorio")
    private Long ownerId;

    // Constructores
    public CreateOrganizationDTO() {
    }

    public CreateOrganizationDTO(String legalName, String commercialName, String ruc, Long ownerId) {
        this.legalName = legalName;
        this.commercialName = commercialName;
        this.ruc = ruc;
        this.ownerId = ownerId;
    }

    // Getters y Setters
    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public String getCommercialName() {
        return commercialName;
    }

    public void setCommercialName(String commercialName) {
        this.commercialName = commercialName;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}
