// Paquete actualizado para el nuevo microservicio
package com.galaxiawonder.propgms.organizations.domain.model.valueobjects;

/**
 * Represents the status of an organization invitation.
 *
 * It provides three possible statuses:
 * - PENDING: The invitation has been sent but not yet responded to.
 * - ACCEPTED: The recipient accepted the invitation.
 * - REJECTED: The recipient explicitly rejected the invitation.
 */
public enum OrganizationInvitationStatuses {
    PENDING,
    ACCEPTED,
    REJECTED
}