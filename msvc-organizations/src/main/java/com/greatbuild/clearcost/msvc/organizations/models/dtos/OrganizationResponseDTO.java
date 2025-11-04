package com.greatbuild.clearcost.msvc.organizations.models.dtos;

import java.time.LocalDate;

/**
 * DTO para respuesta de organización
 * No incluye la lista completa de members para evitar JSON gigantes
 * Los members se consultan en un endpoint separado
 */
public class OrganizationResponseDTO {

    private Long id;
    private String legalName;
    private String commercialName;
    private String ruc;
    private Long ownerId;
    private LocalDate createdAt;
    private Integer memberCount; // Cantidad de miembros (sin incluir el owner)

    // Constructores
    public OrganizationResponseDTO() {
    }

    public OrganizationResponseDTO(Long id, String legalName, String commercialName, 
                                   String ruc, Long ownerId, LocalDate createdAt, Integer memberCount) {
        this.id = id;
        this.legalName = legalName;
        this.commercialName = commercialName;
        this.ruc = ruc;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
        this.memberCount = memberCount;
    }

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
}
