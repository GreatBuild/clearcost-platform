// Paquete actualizado para el nuevo microservicio
package com.galaxiawonder.propgms.organizations.domain.model.commands;

/**
 * AcceptInvitationCommand
 *
 * @summary
 * Command used to accept a pending organization invitation.
 *
 * @param invitationId the unique identifier of the invitation to be accepted
 *
 * @author
 * Galaxia Wonder Development Team
 * @since 1.0
 */
public record AcceptInvitationCommand(
        Long invitationId
) {}