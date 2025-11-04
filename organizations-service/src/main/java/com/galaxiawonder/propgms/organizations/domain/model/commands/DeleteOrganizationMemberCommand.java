// Paquete actualizado para el nuevo microservicio
package com.galaxiawonder.propgms.organizations.domain.model.commands;


import com.galaxiawonder.propgms.organizations.domain.model.entities.OrganizationMember;

/**
 * Command used to request the deletion of an {@link OrganizationMember} from the system.
 *
 * @param organizationMemberId the unique ID of the member to be removed
 *
 * @author
 * Galaxia Wonder Development Team
 * @since 1.0
 */
public record DeleteOrganizationMemberCommand(
        Long organizationMemberId
) {
}