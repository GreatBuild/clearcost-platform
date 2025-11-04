// Paquete actualizado para el nuevo microservicio
package com.galaxiawonder.propgms.organizations.domain.model.queries;


import com.galaxiawonder.propgms.organizations.domain.model.aggregates.Organization;
import com.galaxiawonder.propgms.organizations.domain.model.entities.OrganizationMember;

/**
 * Query object usado para recuperar todos los {@link OrganizationMember}
 * asociados con una {@link Organization} específica, por su ID.
 *
 * @param organizationId el identificador único de la organización
 * @since 1.0
 */
public record GetAllMembersByOrganizationIdQuery(
        Long organizationId
) {
}