// Paquete actualizado para el nuevo microservicio
package com.galaxiawonder.propgms.organizations.domain.model.queries;

/**
 * @summary
 * Esta clase representa la query para obtener una organización por su id.
 * @param id - el id de la organización.
 */
public record GetOrganizationByIdQuery(Long id) {
    public GetOrganizationByIdQuery {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
    }
}