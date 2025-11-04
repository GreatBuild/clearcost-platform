// Paquete actualizado para el nuevo microservicio
package com.galaxiawonder.propgms.organizations.domain.model.queries;

/**
 * Query object usado para solicitar todas las invitaciones de organización
 * asociadas con una persona específica.
 *
 * @param personId el identificador único de la persona
 * @since 1.0
 */
public record GetAllInvitationsByPersonIdQuery(
        Long personId
) {
}