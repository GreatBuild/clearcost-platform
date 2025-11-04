// Paquete actualizado para el nuevo microservicio
package com.galaxiawonder.propgms.organizations.shared.domain.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

/**
 * Modelo base auditable para entidades "hijas" (no agregados raíz).
 * Proporciona ID y campos de auditoría (createdAt, updatedAt).
 */
@EntityListeners(AuditingEntityListener.class) // Habilita la auditoría de JPA
@MappedSuperclass // Indica que es una clase base, no una entidad propia
public class AuditableModel {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Date createdAt;

    @Getter
    @LastModifiedDate
    @Column(nullable = false)
    private Date updatedAt;
}