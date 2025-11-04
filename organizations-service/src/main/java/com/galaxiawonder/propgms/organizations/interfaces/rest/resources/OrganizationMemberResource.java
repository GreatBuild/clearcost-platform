// Paquete actualizado
package com.galaxiawonder.propgms.organizations.interfaces.rest.resources;

import java.util.Date;

public record OrganizationMemberResource(
        Long id,
        String fullName,
        String memberType,
        Date joinedAt
) {
}