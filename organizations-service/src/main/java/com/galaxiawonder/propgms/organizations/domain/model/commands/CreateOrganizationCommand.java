// Paquete actualizado para el nuevo microservicio
package com.galaxiawonder.propgms.organizations.domain.model.commands;

import jakarta.annotation.Nullable;

/**
 * CreateOrganizationCommand
 *
 * @summary
 * Command object que representa los datos para crear una nueva organización.
 *
 * @param legalName the legal projectName of the organization
 * @param commercialName the commercial projectName of the organization (optional)
 * @param ruc the tax identification number (RUC) of the organization
 * @param createdBy the ID of the creator
 */
public record CreateOrganizationCommand(String legalName, @Nullable String commercialName, String ruc, Long createdBy) {

    /**
     * Valida los campos requeridos.
     *
     * @throws IllegalArgumentException si legalName es nulo o vacío
     * @throws IllegalArgumentException si ruc es nulo o vacío
     * @throws IllegalArgumentException si createdBy es nulo
     */
    public CreateOrganizationCommand {
        if (legalName == null || legalName.isBlank()) {
            throw new IllegalArgumentException("legalName cannot be null or blank");
        }
        if (ruc == null || ruc.isBlank()) {
            throw new IllegalArgumentException("ruc cannot be null or blank");
        }
        if (createdBy == null) {
            throw new IllegalArgumentException("createdBy cannot be null");
        }
    }
}