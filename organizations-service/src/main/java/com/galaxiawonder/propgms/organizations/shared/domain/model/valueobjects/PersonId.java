// El paquete se ha actualizado a la estructura de nuestro microservicio
package com.galaxiawonder.propgms.organizations.shared.domain.model.valueobjects;


import jakarta.persistence.Embeddable;

/**
 * PersonId
 *
 * @summary
 * Value object que encapsula el identificador de una Persona.
 * Mejora la expresividad del dominio y asegura la seguridad de tipos.
 *
 * @param personId el identificador numérico de la persona, debe ser positivo y no nulo
 *
 * @since 1.0
 */
@Embeddable // Indica a JPA que esta clase puede ser incrustada en otras entidades
public record PersonId(Long personId) {

    /**
     * Valida el {@code personId}.
     *
     * @throws IllegalArgumentException si {@code personId} es nulo o menor que 1
     */
    public PersonId {
        if (personId == null || personId < 1) {
            throw new IllegalArgumentException("Profile id cannot be null or less than 1");
        }
    }

    /**
     * Constructor por defecto requerido por JPA.
     */
    public PersonId() {
        this(null);
    }
}