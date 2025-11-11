package com.greatbuild.clearcost.msvc.projects.models.dtos;

import com.greatbuild.clearcost.msvc.projects.models.enums.ProjectStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para Project
 */
public class ProjectResponseDTO {

    private Long id;
    private String projectName;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long organizationId;
    private Long contractingEntityId;
    private ProjectStatus status;
    private LocalDateTime createdAt;
    private int memberCount;

    // Constructors
    public ProjectResponseDTO() {
    }

    public ProjectResponseDTO(Long id, String projectName, String description, LocalDate startDate, 
                            LocalDate endDate, Long organizationId, Long contractingEntityId, 
                            ProjectStatus status, LocalDateTime createdAt, int memberCount) {
        this.id = id;
        this.projectName = projectName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.organizationId = organizationId;
        this.contractingEntityId = contractingEntityId;
        this.status = status;
        this.createdAt = createdAt;
        this.memberCount = memberCount;
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

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }
}
