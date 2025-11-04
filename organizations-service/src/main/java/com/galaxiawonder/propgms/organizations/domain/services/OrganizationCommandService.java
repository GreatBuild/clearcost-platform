// Paquete actualizado para el nuevo microservicio
package com.galaxiawonder.propgms.organizations.domain.services;


import com.galaxiawonder.propgms.organizations.domain.model.aggregates.Organization;
import com.galaxiawonder.propgms.organizations.domain.model.commands.*;
import com.galaxiawonder.propgms.organizations.domain.model.entities.OrganizationInvitation;
import com.galaxiawonder.propgms.organizations.domain.model.entities.OrganizationMember;
import com.galaxiawonder.propgms.organizations.shared.domain.model.valueobjects.PersonId;
import com.galaxiawonder.propgms.organizations.shared.domain.model.valueobjects.ProfileDetails;

import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Optional;

/**
 * @name OrganizationCommandService
 * @summary
 * Esta interfaz representa el servicio para manejar los comandos de organización.
 */
public interface OrganizationCommandService {

    Optional<Organization> handle(CreateOrganizationCommand command);

    void handle(DeleteOrganizationCommand command);

    Optional<Organization> handle(UpdateOrganizationCommand command);

    Optional<Triple<Organization, OrganizationInvitation, ProfileDetails>> handle(InvitePersonToOrganizationByEmailCommand command);

    Optional<Triple<Organization, OrganizationInvitation, ProfileDetails>> handle(AcceptInvitationCommand command);

    Optional<Triple<Organization, OrganizationInvitation, ProfileDetails>> handle(RejectInvitationCommand rejectInvitationCommand);

    void handle(DeleteOrganizationMemberCommand command);
}