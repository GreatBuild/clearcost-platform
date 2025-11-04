// Paquete actualizado
package com.galaxiawonder.propgms.organizations.interfaces.rest.resources;

import java.util.Date;

public record OrganizationResource(
        Long id,
        String legalName,
        String commercialName,
        String ruc,
        Long createdBy,
        String status,
        Date createdAt
) {
}