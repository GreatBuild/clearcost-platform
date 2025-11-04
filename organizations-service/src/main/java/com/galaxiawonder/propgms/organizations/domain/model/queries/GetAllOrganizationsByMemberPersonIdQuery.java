// Paquete actualizado para el nuevo microservicio
package com.galaxiawonder.propgms.organizations.domain.model.queries;


import com.galaxiawonder.propgms.organizations.domain.model.aggregates.Organization;
import com.galaxiawonder.propgms.organizations.domain.model.entities.OrganizationMember;

/**
 * Query object usado para recuperar todas las {@link Organization}
 * en las que una persona específica está registrada como miembro.
 *
 * @param personId el identificador único de la persona
 * @since 1.0
 */
public record GetAllOrganizationsByMemberPersonIdQuery(
        Long personId
) {
}