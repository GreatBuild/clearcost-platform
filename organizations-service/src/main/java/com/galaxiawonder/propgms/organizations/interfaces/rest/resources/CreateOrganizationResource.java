// Paquete actualizado
package com.galaxiawonder.propgms.organizations.interfaces.rest.resources;

/**
 * Resource que representa los datos para crear una organización.
 */
public record CreateOrganizationResource(String legalName, String commercialName, String ruc, Long createdBy) {
    public CreateOrganizationResource {
        if (legalName == null || legalName.isBlank()) throw new IllegalArgumentException("legalName cannot be null or empty");
        if (ruc == null || ruc.isBlank()) throw new IllegalArgumentException("RUC cannot be null or empty");
        if (createdBy == null) throw new IllegalArgumentException("createdBy cannot be null or empty");
    }
}