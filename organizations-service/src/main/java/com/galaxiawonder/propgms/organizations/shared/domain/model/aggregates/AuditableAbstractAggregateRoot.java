// El paquete se ha actualizado a la estructura de nuestro microservicio
package com.galaxiawonder.propgms.organizations.shared.domain.model.aggregates;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

/**
 * Clase base para todos los agregados raíz que requieren auditoría.
 *
 * @param <T> el tipo de agregado raíz
 * @summary Esta clase abstracta extiende {@link AbstractAggregateRoot} y añade
 * campos de auditoría (id, createdAt, updatedAt).
 */
@Getter
@EntityListeners(AuditingEntityListener.class) // Habilita la auditoría de JPA
@MappedSuperclass // Indica a JPA que esta clase no es una entidad en sí, sino una base para otras
public class AuditableAbstractAggregateRoot<T extends AbstractAggregateRoot<T>> extends AbstractAggregateRoot<T> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate // Marcado para ser poblado en la creación
    @Column(nullable = false, updatable = false)
    private Date createdAt;

    @LastModifiedDate // Marcado para ser poblado en la actualización
    @Column(nullable = false)
    private Date updatedAt;

    /**
     * Registra un evento de dominio.
     *
     * @param event el evento de dominio a registrar
     */
    public void addDomainEvent(Object event) {
        super.registerEvent(event);
    }
}