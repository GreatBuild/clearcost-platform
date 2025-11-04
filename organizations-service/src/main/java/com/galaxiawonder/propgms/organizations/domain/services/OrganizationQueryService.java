// Paquete actualizado para el nuevo microservicio
package com.galaxiawonder.propgms.organizations.domain.services;


import com.galaxiawonder.propgms.organizations.domain.model.aggregates.Organization;
import com.galaxiawonder.propgms.organizations.domain.model.entities.OrganizationInvitation;
import com.galaxiawonder.propgms.organizations.domain.model.entities.OrganizationInvitationStatus;
import com.galaxiawonder.propgms.organizations.domain.model.entities.OrganizationMember;
import com.galaxiawonder.propgms.organizations.domain.model.queries.*;
import com.galaxiawonder.propgms.organizations.domain.model.valueobjects.OrganizationInvitationStatuses;
import com.galaxiawonder.propgms.organizations.shared.domain.model.valueobjects.ProfileDetails;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Optional;

/**
 * OrganizationQueryService
 *
 * @summary
 * Esta interfaz representa el servicio para manejar las queries de organización.
 */
public interface OrganizationQueryService {

    Optional<Organization> handle(GetOrganizationByIdQuery query);

    List<ImmutablePair<OrganizationInvitation, ProfileDetails>> handle(GetAllInvitationsByOrganizationIdQuery query);

    List<OrganizationMember> handle(GetAllMembersByOrganizationIdQuery query);

    List<Organization> handle(GetAllOrganizationsByMemberPersonIdQuery query);

    List<Triple<Organization, OrganizationInvitation, ProfileDetails>> handle(GetAllInvitationsByPersonIdQuery query);

    Optional<List<Organization>> handle(GetAllOrganizationsQuery query);
}