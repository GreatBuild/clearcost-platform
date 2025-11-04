// Paquete actualizado para el nuevo microservicio
package com.galaxiawonder.propgms.organizations.shared.domain.model.valueobjects;



import jakarta.persistence.Embeddable;

/**
 * OrganizationId
 *
 * @summary
 * Value object que encapsula el identificador de una Organización.
 *
 * @param organizationId el identificador numérico de la organización, debe ser positivo y no nulo
 *
 * @since 1.0
 */
@Embeddable
public record OrganizationId(Long organizationId) {

    /**
     * Constructor por defecto requerido por JPA.
     */
    public OrganizationId() {
        this(null);
    }

    /**
     * Valida el {@code organizationId}.
     *
     * @throws IllegalArgumentException if {@code organizationId} es nulo o menor que 1
     */
    public OrganizationId {
        if (organizationId == null || organizationId < 1) {
            throw new IllegalArgumentException("Organization id cannot be null or less than 1");
        }
    }
}