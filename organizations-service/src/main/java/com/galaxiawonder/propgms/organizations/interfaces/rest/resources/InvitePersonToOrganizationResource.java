// Paquete actualizado
package com.galaxiawonder.propgms.organizations.interfaces.rest.resources;

/**
 * Resource que representa la petición para invitar a una persona.
 *
 * @param organizationId el ID de la organización
 * @param email el email de la persona a invitar
 */
public record InvitePersonToOrganizationResource(
        Long organizationId,
        String email
) {
}