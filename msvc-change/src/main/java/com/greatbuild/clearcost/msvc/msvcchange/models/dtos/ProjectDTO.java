package com.greatbuild.clearcost.msvc.msvcchange.models.dtos;

import java.time.LocalDate;

/**
 * DTO para recibir datos de proyecto desde msvc-projects
 */
public class ProjectDTO {

    private Long id;
    private String projectName;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long organizationId;
    private Long contractingEntityId;
    private String status;
    private LocalDate createdAt;
    private Integer memberCount;

    // Constructors
    public ProjectDTO() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
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

    public Long getContractingEntityId() {
        return contractingEntityId;
    }

    public void setContractingEntityId(Long contractingEntityId) {
        this.contractingEntityId = contractingEntityId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
