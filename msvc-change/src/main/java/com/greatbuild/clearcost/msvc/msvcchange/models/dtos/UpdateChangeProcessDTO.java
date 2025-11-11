package com.greatbuild.clearcost.msvc.msvcchange.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para actualizar (responder) una solicitud de cambio
 */
public class UpdateChangeProcessDTO {

    @NotBlank(message = "El response es obligatorio")
    private String response;

    @NotBlank(message = "El status es obligatorio")
    private String status;  // APPROVED o REJECTED

    // Constructors
    public UpdateChangeProcessDTO() {
    }

    // Getters and Setters
    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
