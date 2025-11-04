// Paquete actualizado para el nuevo microservicio
package com.galaxiawonder.propgms.organizations.application.internal.commandservices;


import com.galaxiawonder.propgms.organizations.domain.model.aggregates.Organization;
import com.galaxiawonder.propgms.organizations.domain.model.commands.*;
import com.galaxiawonder.propgms.organizations.domain.model.entities.*;
import com.galaxiawonder.propgms.organizations.domain.model.valueobjects.OrganizationInvitationStatuses;
import com.galaxiawonder.propgms.organizations.domain.model.valueobjects.OrganizationMemberTypes;
import com.galaxiawonder.propgms.organizations.domain.model.valueobjects.OrganizationStatuses;
import com.galaxiawonder.propgms.organizations.domain.model.valueobjects.Ruc;
import com.galaxiawonder.propgms.organizations.domain.services.OrganizationCommandService;
import com.galaxiawonder.propgms.organizations.iam.interfaces.acl.IAMContextFacade; // <- Importa nuestra nueva fachada ACL
import com.galaxiawonder.propgms.organizations.infrastructure.persistence.jpa.repositories.*;
import com.galaxiawonder.propgms.organizations.shared.domain.model.valueobjects.PersonId;
import com.galaxiawonder.propgms.organizations.shared.domain.model.valueobjects.ProfileDetails;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrganizationCommandServiceImpl implements OrganizationCommandService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationStatusRepository organizationStatusRepository;
    private final OrganizationInvitationStatusRepository organizationInvitationStatusRepository;
    private final OrganizationMemberTypeRepository organizationMemberTypeRepository;
    private final OrganizationInvitationRepository organizationInvitationRepository;

    // La dependencia clave, spring inyectará nuestro IAMContextFacadeStub aquí.
    private final IAMContextFacade iamContextFacade;

    public OrganizationCommandServiceImpl(
            OrganizationRepository organizationRepository,
            OrganizationStatusRepository organizationStatusRepository,
            OrganizationInvitationStatusRepository organizationInvitationStatusRepository,
            OrganizationMemberTypeRepository organizationMemberTypeRepository,
            IAMContextFacade iamContextFacade, // Funciona gracias al Stub
            OrganizationInvitationRepository organizationInvitationRepository
    ) {
        this.organizationRepository = organizationRepository;
        this.organizationStatusRepository = organizationStatusRepository;
        this.iamContextFacade = iamContextFacade;
        this.organizationInvitationStatusRepository = organizationInvitationStatusRepository;
        this.organizationMemberTypeRepository = organizationMemberTypeRepository;
        this.organizationInvitationRepository = organizationInvitationRepository;
    }

    @Override
    public Optional<Organization> handle(CreateOrganizationCommand command){
        if(organizationRepository.existsByRuc(new Ruc(command.ruc())))
            throw new IllegalArgumentException("Organization with same RUC already exists for this API key");

        OrganizationStatus status = getOrganizationStatus(OrganizationStatuses.ACTIVE);
        OrganizationMemberType contractorType = getOrganizationMemberType(OrganizationMemberTypes.CONTRACTOR);

        // Aquí se llama a nuestro Stub, que devolverá un perfil falso
        var contractorProfileDetails = iamContextFacade.getProfileDetailsById(command.createdBy());

        var organization = new Organization(command, status, contractorType, contractorProfileDetails);

        var createdOrganization = organizationRepository.save(organization);
        return Optional.of(createdOrganization);
    }

    @Override
    public void handle(DeleteOrganizationCommand command) {
        Ruc ruc = new Ruc(command.ruc());
        if (!organizationRepository.existsByRuc(ruc)) {
            throw new IllegalArgumentException("Organization doesn't exist");
        }
        Organization organization = organizationRepository.findByRuc(ruc);
        organizationRepository.delete(organization);
    }

    @Override
    public Optional<Organization> handle(UpdateOrganizationCommand command) {
        var result = organizationRepository.findById(command.organizationId());
        if (result.isEmpty())
            throw new IllegalArgumentException("Organization doesn't exist");
        var organizationToUpdate = result.get();
        try{
            var updatedOrganization = organizationRepository.save(organizationToUpdate.updateInformation(command.commercialName(), command.legalName()));
            return Optional.of(updatedOrganization);
        } catch (Exception e){
            throw new IllegalArgumentException("Error while updating organization: %s".formatted(e.getMessage()));
        }
    }

    @Transactional
    @Override
    public Optional<Triple<Organization, OrganizationInvitation, ProfileDetails>> handle(
            InvitePersonToOrganizationByEmailCommand command) {

        // Aquí se llama a nuestro Stub, que devolverá un ID falso
        var personId = new PersonId(this.iamContextFacade.getPersonIdFromEmail(command.email()));

        Organization organization = this.organizationRepository.findById(command.organizationId())
                .orElseThrow(() -> new EntityNotFoundException("Organization doesn't exist"));

        OrganizationInvitationStatus pendingStatus = getOrganizationInvitationStatus(OrganizationInvitationStatuses.PENDING);

        organization.addInvitation(personId, pendingStatus);
        saveOrganization(organization);

        var persistedInvitation = organizationInvitationRepository
                .findTopByOrganizationIdAndInvitedPersonIdOrderByIdDesc(
                        organization.getId(), personId
                )
                .orElseThrow(() -> new IllegalStateException("Failed to persist invitation"));

        return returnInvitationTripleResult(organization, persistedInvitation);
    }

    @Override
    public Optional<Triple<Organization, OrganizationInvitation, ProfileDetails>> handle(AcceptInvitationCommand command) {
        Organization organization = this.organizationRepository.findOrganizationByInvitationId(command.invitationId())
                .orElseThrow(()-> new EntityNotFoundException("No organization found for the given invitation id: " + command.invitationId()));

        OrganizationInvitationStatus acceptedStatus = getOrganizationInvitationStatus(OrganizationInvitationStatuses.ACCEPTED);
        OrganizationMemberType workerType = getOrganizationMemberType(OrganizationMemberTypes.WORKER);

        OrganizationInvitation invitation = organization.selectInvitationFromId(command.invitationId());

        // Aquí se llama a nuestro Stub, que devolverá un perfil falso
        var profileDetails = iamContextFacade.getProfileDetailsById(invitation.getInvitedPersonId().personId());

        OrganizationInvitation acceptInvitation = organization.acceptInvitation(command.invitationId(), acceptedStatus, workerType, profileDetails);
        saveOrganization(organization);

        return returnInvitationTripleResult(organization, acceptInvitation);
    }

    @Override
    public Optional<Triple<Organization, OrganizationInvitation, ProfileDetails>> handle(RejectInvitationCommand rejectInvitationCommand) {
        Organization organization = this.organizationRepository.findOrganizationByInvitationId(rejectInvitationCommand.invitationId())
                .orElseThrow(() -> new EntityNotFoundException("No organization found for the given invitation id: " + rejectInvitationCommand.invitationId()));

        OrganizationInvitationStatus rejectedStatus = getOrganizationInvitationStatus(OrganizationInvitationStatuses.REJECTED);
        OrganizationInvitation invitation = organization.rejectInvitation(rejectInvitationCommand.invitationId(), rejectedStatus);
        saveOrganization(organization);

        return returnInvitationTripleResult(organization, invitation);
    }

    @Override
    public void handle(DeleteOrganizationMemberCommand command) {
        Organization organization = organizationRepository.findOrganizationByMemberId(command.organizationMemberId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No organization found for the given organization member ID: " + command.organizationMemberId()));

        PersonId removedPersonId = organization.getMembers().stream()
                .filter(member -> member.getId().equals(command.organizationMemberId()))
                .findFirst()
                .map(OrganizationMember::getPersonId)
                .orElseThrow(() -> new IllegalStateException("Member not found in the organization"));

        organization.removeMemberById(command.organizationMemberId());
        organization.removeInvitationsByPersonId(removedPersonId);
        saveOrganization(organization);
    }

    private void saveOrganization(Organization organization) {
        organizationRepository.save(organization);
    }

    private OrganizationStatus getOrganizationStatus(OrganizationStatuses status) {
        return organizationStatusRepository.findByName(status)
                .orElseThrow(() -> new IllegalStateException("Default status 'ACTIVE' not found"));
    }

    private OrganizationInvitationStatus getOrganizationInvitationStatus(OrganizationInvitationStatuses status) {
        return this.organizationInvitationStatusRepository.findByName(status)
                .orElseThrow(() -> new IllegalStateException("Organization invitation status"));
    }

    private OrganizationMemberType getOrganizationMemberType(OrganizationMemberTypes status) {
        return this.organizationMemberTypeRepository.findByName(status)
                .orElseThrow(() -> new IllegalStateException("Organization member type not found"));
    }

    private Optional<Triple<Organization, OrganizationInvitation, ProfileDetails>> returnInvitationTripleResult(
            Organization organization, OrganizationInvitation invitation) {

        ProfileDetails profileDetails = getContactorProfileDetails(organization);
        return Optional.of(Triple.of(organization, invitation, profileDetails));
    }

    private ProfileDetails getContactorProfileDetails(Organization organization) {
        // Aquí se llama a nuestro Stub, que devolverá un perfil falso
        return iamContextFacade.getProfileDetailsById(organization.getCreatedBy().personId());
    }
}