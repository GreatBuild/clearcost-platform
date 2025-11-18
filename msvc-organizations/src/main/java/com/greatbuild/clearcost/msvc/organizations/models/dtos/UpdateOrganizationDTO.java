package com.greatbuild.clearcost.msvc.organizations.models.dtos;

/**
 * DTO para actualización parcial de una organización
 * Todos los campos son opcionales (nullable)
 * Solo se actualizan los campos que no son null
 */
public class UpdateOrganizationDTO {

    private String legalName;
    private String commercialName;
    private String ruc;
    // NO incluimos ownerId - el dueño no debe cambiar

    public UpdateOrganizationDTO() {
    }

    public UpdateOrganizationDTO(String legalName, String commercialName, String ruc) {
        this.legalName = legalName;
        this.commercialName = commercialName;
        this.ruc = ruc;
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
}
