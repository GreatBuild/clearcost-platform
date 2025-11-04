// Paquete actualizado para el nuevo microservicio
package com.galaxiawonder.propgms.organizations.domain.model.commands;

/**
 * Command to delete an organization
 * @param ruc the ruc of an organization.
 * Cannot be null.
 */
public record DeleteOrganizationCommand(String ruc) {
    /**
     * Constructor
     * @param ruc the ruc of an organization.
     * Cannot be null.
     * @throws IllegalArgumentException if ruc is null
     */
    public DeleteOrganizationCommand {
        if (ruc == null) {
            throw new IllegalArgumentException("Ruc cannot be null");
        }
    }
}