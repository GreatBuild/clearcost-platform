// Paquete actualizado para el nuevo microservicio
package com.galaxiawonder.propgms.organizations.application.internal.queryservices;


import com.galaxiawonder.propgms.organizations.domain.model.aggregates.Organization;
import com.galaxiawonder.propgms.organizations.domain.model.entities.OrganizationInvitation;
import com.galaxiawonder.propgms.organizations.domain.model.entities.OrganizationMember;
import com.galaxiawonder.propgms.organizations.domain.model.queries.*;
import com.galaxiawonder.propgms.organizations.domain.services.OrganizationQueryService;
import com.galaxiawonder.propgms.organizations.iam.interfaces.acl.IAMContextFacade; // <- Importa nuestra nueva fachada ACL
import com.galaxiawonder.propgms.organizations.infrastructure.persistence.jpa.repositories.OrganizationRepository;
import com.galaxiawonder.propgms.organizations.shared.domain.model.valueobjects.PersonId;
import com.galaxiawonder.propgms.organizations.shared.domain.model.valueobjects.ProfileDetails;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrganizationQueryServiceImpl implements OrganizationQueryService {

    private final OrganizationRepository organizationRepository;

    // La dependencia clave, spring inyectará nuestro IAMContextFacadeStub aquí.
    private final IAMContextFacade iamContextFacade;

    public OrganizationQueryServiceImpl(OrganizationRepository organizationRepository, IAMContextFacade iamContextFacade) {
        this.organizationRepository = organizationRepository;
        this.iamContextFacade = iamContextFacade;
    }

    @Override
    public Optional<Organization> handle(GetOrganizationByIdQuery query){
        return organizationRepository.findById(query.id());
    }

    @Override
    public List<ImmutablePair<OrganizationInvitation, ProfileDetails>> handle(GetAllInvitationsByOrganizationIdQuery query) {
        Organization organization = organizationRepository.findById(query.organizationId())
                .orElseThrow(() -> new IllegalArgumentException("No organization found by the given ID: " + query.organizationId()));

        List<OrganizationInvitation> invitations = new ArrayList<>(organization.getInvitations());

        Set<PersonId> members = organization.getMembers().stream()
                .map(OrganizationMember::getPersonId)
                .collect(Collectors.toSet());

        Collections.reverse(invitations); // newest to oldest
        Set<PersonId> seen = new HashSet<>();

        return invitations.stream()
                .filter(inv -> !members.contains(inv.getInvitedPersonId()))
                .filter(inv -> seen.add(inv.getInvitedPersonId()))
                .map(inv -> {
                    // Aquí se llama a nuestro Stub, que devolverá un perfil falso
                    ProfileDetails profileDetails = iamContextFacade.getProfileDetailsById(inv.getInvitedPersonId().personId());
                    return ImmutablePair.of(inv, profileDetails);
                })
                .toList();
    }

    @Override
    public List<OrganizationMember> handle(GetAllMembersByOrganizationIdQuery query) {
        Organization organization = organizationRepository.findById(query.organizationId())
                .orElseThrow(() -> new IllegalArgumentException("No organization found by the given ID: " + query.organizationId()));

        return organization.getMembers();
    }

    @Override
    public List<Organization> handle(GetAllOrganizationsByMemberPersonIdQuery query) {
        return organizationRepository.findAllOrganizationsByOrganizationMemberPersonId(query.personId())
                .orElseThrow(()-> new IllegalArgumentException("The person with the ID " + query.personId() + " does not belong to any organization"));
    }

    @Override
    public List<Triple<Organization, OrganizationInvitation, ProfileDetails>> handle(GetAllInvitationsByPersonIdQuery query) {
        Long personId = query.personId();
        List<Organization> organizations = organizationRepository.findAll();

        return organizations.stream()
                .flatMap(org -> org.getInvitations().stream()
                        .filter(invitation ->
                                invitation.getInvitedPersonId().personId().equals(personId)
                                        && invitation.isPending())
                        .map(invitation -> {
                            // Aquí se llama a nuestro Stub, que devolverá un perfil falso
                            ProfileDetails profile = iamContextFacade.getProfileDetailsById(org.getCreatedBy().personId());
                            return Triple.of(org, invitation, profile);
                        }))
                .toList();
    }

    @Override
    public Optional<List<Organization>> handle(GetAllOrganizationsQuery query){
        return Optional.of(organizationRepository.findAll());
    }
}