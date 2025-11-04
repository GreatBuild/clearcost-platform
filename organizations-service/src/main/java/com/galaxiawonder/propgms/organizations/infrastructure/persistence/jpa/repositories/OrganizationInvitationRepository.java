// Paquete actualizado para el nuevo microservicio
package com.galaxiawonder.propgms.organizations.infrastructure.persistence.jpa.repositories;


import com.galaxiawonder.propgms.organizations.domain.model.entities.OrganizationInvitation;
import com.galaxiawonder.propgms.organizations.shared.domain.model.valueobjects.PersonId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationInvitationRepository extends JpaRepository<OrganizationInvitation, Long> {

    /**
     * Recupera todas las {@link OrganizationInvitation} asociadas con el ID de la persona invitada.
     *
     * @param id el identificador único de la persona invitada
     * @return una {@link List} de {@link OrganizationInvitation}
     */
    List<OrganizationInvitation> findAllByInvitedPersonId(Long id);

    /**
     * Recupera la {@link OrganizationInvitation} más reciente para una organización y persona invitada.
     *
     * @param organization_id el ID de la organización
     * @param invitedPersonId el {@link PersonId} de la persona invitada
     * @return un {@link Optional} conteniendo la última {@link OrganizationInvitation} si se encuentra
     */
    Optional<OrganizationInvitation> findTopByOrganizationIdAndInvitedPersonIdOrderByIdDesc(
            Long organization_id, PersonId invitedPersonId);
}