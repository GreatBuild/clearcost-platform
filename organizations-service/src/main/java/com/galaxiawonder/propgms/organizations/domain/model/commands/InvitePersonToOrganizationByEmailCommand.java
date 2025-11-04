// Paquete actualizado para el nuevo microservicio
package com.galaxiawonder.propgms.organizations.domain.model.commands;

/**
 * InvitePersonToOrganizationByEmailCommand
 *
 * @summary
 * Command used to invite a person to an organization using their email address.
 *
 * @param organizationId the ID of the target organization
 * @param email the email address of the person to be invited
 *
 * @author
 * Galaxia Wonder Development Team
 * @since 1.0
 */
public record InvitePersonToOrganizationByEmailCommand(
        Long organizationId,
        String email
) {}