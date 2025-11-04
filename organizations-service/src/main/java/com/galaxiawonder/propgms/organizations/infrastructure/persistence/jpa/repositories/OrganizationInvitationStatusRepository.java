// Paquete actualizado para el nuevo microservicio
package com.galaxiawonder.propgms.organizations.infrastructure.persistence.jpa.repositories;


import com.galaxiawonder.propgms.organizations.domain.model.entities.OrganizationInvitationStatus;
import com.galaxiawonder.propgms.organizations.domain.model.valueobjects.OrganizationInvitationStatuses;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationInvitationStatusRepository extends JpaRepository<OrganizationInvitationStatus, Long> {

    /**
     * Busca un estado de invitación por su nombre de enum.
     *
     * @param name el enum {@link OrganizationInvitationStatuses}
     * @return un {@link Optional} conteniendo el {@link OrganizationInvitationStatus} si se encuentra
     */
    Optional<OrganizationInvitationStatus> findByName(OrganizationInvitationStatuses name);

    /**
     * Verifica si existe un estado de invitación por su nombre de enum.
     *
     * @param name el enum {@link OrganizationInvitationStatuses}
     * @return true si existe, false de lo contrario
     */
    boolean existsByName(OrganizationInvitationStatuses name);
}