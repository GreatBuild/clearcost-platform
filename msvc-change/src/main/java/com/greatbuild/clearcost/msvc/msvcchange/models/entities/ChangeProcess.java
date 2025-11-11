package com.greatbuild.clearcost.msvc.msvcchange.models.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Entidad para solicitudes de cambio en proyectos
 */
@Entity
@Table(name = "change_processes")
public class ChangeProcess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long projectId;

    @Column(nullable = false)
    private Long createdBy;

    @Column(nullable = false, length = 50)
    private String origin;  // CHANGE_REQUEST o TECHNICAL_QUERY

    @Column(nullable = false)
    private Long statusId;  // 1=PENDING, 2=APPROVED, 3=REJECTED

    @Column(nullable = false, columnDefinition = "TEXT")
    private String justification;

    @Column(columnDefinition = "TEXT")
    private String response;

    @Column(nullable = false)
    private LocalDate createdAt;

    private LocalDate updatedAt;

    // Constructors
    public ChangeProcess() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }
}
