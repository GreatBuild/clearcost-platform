// Paquete actualizado
package com.galaxiawonder.propgms.organizations.interfaces.rest.assemblers;


import com.galaxiawonder.propgms.organizations.domain.model.aggregates.Organization;
import com.galaxiawonder.propgms.organizations.domain.model.entities.OrganizationInvitation;
import com.galaxiawonder.propgms.organizations.interfaces.rest.resources.OrganizationInvitationResource;
import com.galaxiawonder.propgms.organizations.shared.domain.model.valueobjects.ProfileDetails;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Triple;

public class OrganizationInvitationResourceFromEntityAssembler {

    public static OrganizationInvitationResource toResourceFromEntity(
            Triple<Organization, OrganizationInvitation, ProfileDetails> triple) {

        Organization organization = triple.getLeft();
        OrganizationInvitation invitation = triple.getMiddle();
        ProfileDetails contractorPerson = triple.getRight();

        if (invitation == null) return null;

        return new OrganizationInvitationResource(
                invitation.getId(),
                organization.getCommercialName().commercialName(),
                contractorPerson.name().getFullName(),
                invitation.getStatus().getStringName(),
                invitation.getCreatedAt(),
                null
        );
    }

    public static OrganizationInvitationResource toResourceFromPair(
            ImmutablePair<OrganizationInvitation, ProfileDetails> pair
    ) {
        OrganizationInvitation invitation = pair.getLeft();
        ProfileDetails invitedPerson = pair.getRight();

        return new OrganizationInvitationResource(
                invitation.getId(),
                null,
                null,
                invitation.getStatus().getStringName(),
                invitation.getCreatedAt(),
                invitedPerson.name().getFullName()
        );
    }
}