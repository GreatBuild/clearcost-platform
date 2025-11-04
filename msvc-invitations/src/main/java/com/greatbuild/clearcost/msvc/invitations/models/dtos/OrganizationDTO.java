package com.greatbuild.clearcost.msvc.invitations.models.dtos;

import java.time.LocalDate;

/**
 * DTO para recibir información de organizaciones desde msvc-organizations
 * Debe coincidir con OrganizationResponseDTO de msvc-organizations
 */
public class OrganizationDTO {

    private Long id;
    private String legalName;
    private String commercialName;
    private String ruc;
    private Long ownerId; // Cambio de creatorId a ownerId para coincidir con msvc-organizations
    private LocalDate createdAt;
    private Integer memberCount;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(Integer memberCount) {
        this.memberCount = memberCount;
    }

    // Método helper para mantener compatibilidad con código que usa 'name'
    public String getName() {
        return commercialName != null ? commercialName : legalName;
    }

    // Método helper para mantener compatibilidad con código que usa 'creatorId'
    public Long getCreatorId() {
        return ownerId;
    }
}
