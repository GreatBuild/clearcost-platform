package com.greatbuild.clearcost.msvc.organizations.models.dtos;

import java.time.LocalDate;

/**
 * DTO para la respuesta de organizaciones del usuario
 * Incluye el rol del usuario en la organización
 */
public class UserOrganizationResponseDTO {

    private Long id;
    private String legalName;
    private String commercialName;
    private String ruc;
    private Long ownerId;
    private LocalDate createdAt;
    private int membersCount;
    private String userRole; // "CONTRACTOR" o "MEMBER"

    public UserOrganizationResponseDTO() {
    }

    public UserOrganizationResponseDTO(Long id, String legalName, String commercialName, 
                                       String ruc, Long ownerId, LocalDate createdAt, 
                                       int membersCount, String userRole) {
        this.id = id;
        this.legalName = legalName;
        this.commercialName = commercialName;
        this.ruc = ruc;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
        this.membersCount = membersCount;
        this.userRole = userRole;
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

    public int getMembersCount() {
        return membersCount;
    }

    public void setMembersCount(int membersCount) {
        this.membersCount = membersCount;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
}
