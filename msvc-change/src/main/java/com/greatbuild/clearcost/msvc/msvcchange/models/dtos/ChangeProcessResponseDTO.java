package com.greatbuild.clearcost.msvc.msvcchange.models.dtos;

/**
 * DTO para respuesta de solicitud de cambio
 */
public class ChangeProcessResponseDTO {

    private Long id;
    private String origin;
    private String status;
    private String justification;
    private String response;
    private Long projectId;

    // Constructors
    public ChangeProcessResponseDTO() {
    }

    public ChangeProcessResponseDTO(Long id, String origin, String status, 
                                   String justification, String response, Long projectId) {
        this.id = id;
        this.origin = origin;
        this.status = status;
        this.justification = justification;
        this.response = response;
        this.projectId = projectId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
