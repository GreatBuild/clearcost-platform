// Paquete actualizado para el nuevo microservicio
package com.galaxiawonder.propgms.organizations.infrastructure.persistence.jpa.repositories;


import com.galaxiawonder.propgms.organizations.domain.model.entities.OrganizationStatus;
import com.galaxiawonder.propgms.organizations.domain.model.valueobjects.OrganizationStatuses;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationStatusRepository extends JpaRepository<OrganizationStatus, Long> {

    /**
     * Busca un estado de organización por su nombre.
     *
     * @param name el enum {@link OrganizationStatuses}
     * @return un {@link Optional} conteniendo el {@link OrganizationStatus} si se encuentra
     */
    Optional<OrganizationStatus> findByName(OrganizationStatuses name);

    /**
     * Verifica si existe un estado de organización por su nombre.
     *
     * @param name el enum {@link OrganizationStatuses}
     * @return true si existe, false de lo contrario
     */
    boolean existsByName(OrganizationStatuses name);
}