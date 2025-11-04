// Paquete actualizado para el nuevo microservicio
package com.galaxiawonder.propgms.organizations.domain.model.commands;

/**
 * Command to update an organization
 * @param organizationId the organization id.
 * Cannot be null or less than 1.
 * @param commercialName the commercial projectName of an organization
 * Cannot be null or blank.
 */
public record UpdateOrganizationCommand(
        Long organizationId,
        String commercialName,
        String legalName
) {
    /**
     * Constructor
     * @param organizationId the organization id
     * Cannot be null or less than 1.
     * @param commercialName the commercial projectName of an organization
     * Cannot be null or blank
     * @throws IllegalArgumentException if personId is null or less than 1.
     * @throws IllegalArgumentException if commercial projectName is null or blank
     */
    public UpdateOrganizationCommand {
        if (organizationId == null || organizationId <= 0) {
            throw new IllegalArgumentException("Organization id cannot be null or less than 1");
        }

    }
}