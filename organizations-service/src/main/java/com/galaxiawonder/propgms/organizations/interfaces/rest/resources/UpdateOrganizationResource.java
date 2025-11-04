// Paquete actualizado
package com.galaxiawonder.propgms.organizations.interfaces.rest.resources;

import jakarta.annotation.Nullable;

public record UpdateOrganizationResource(
        @Nullable String commercialName,
        @Nullable String legalName
) {}