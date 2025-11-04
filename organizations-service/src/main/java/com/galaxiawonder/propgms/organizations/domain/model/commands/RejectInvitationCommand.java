// Paquete actualizado para el nuevo microservicio
package com.galaxiawonder.propgms.organizations.domain.model.commands;

/**
 * RejectInvitationCommand
 *
 * @summary
 * Command used to reject a pending organization invitation.
 *
 * @param invitationId the unique identifier of the invitation to be rejected
 *
 * @author
 * Galaxia Wonder Development Team
 * @since 1.0
 */
public record RejectInvitationCommand(
        Long invitationId
) {}