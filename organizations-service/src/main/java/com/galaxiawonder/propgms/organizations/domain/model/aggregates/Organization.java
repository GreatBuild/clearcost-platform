// Paquete actualizado para el nuevo microservicio
package com.galaxiawonder.propgms.organizations.domain.model.aggregates;


import com.galaxiawonder.propgms.organizations.domain.model.commands.CreateOrganizationCommand;
import com.galaxiawonder.propgms.organizations.domain.model.entities.*;
import com.galaxiawonder.propgms.organizations.domain.model.valueobjects.CommercialName;
import com.galaxiawonder.propgms.organizations.domain.model.valueobjects.LegalName;
import com.galaxiawonder.propgms.organizations.domain.model.valueobjects.OrganizationMemberTypes;
import com.galaxiawonder.propgms.organizations.domain.model.valueobjects.Ruc;
import com.galaxiawonder.propgms.organizations.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.galaxiawonder.propgms.organizations.shared.domain.model.valueobjects.PersonId;
import com.galaxiawonder.propgms.organizations.shared.domain.model.valueobjects.ProfileDetails;

import jakarta.persistence.*; // Import añadido para EntityNotFoundException
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Organization Aggregate Root
 */
@Entity
@Table(name = "organizations")
@EntityListeners(AuditingEntityListener.class)
public class Organization extends AuditableAbstractAggregateRoot<Organization> {
    @Column(nullable = false)
    @Getter
    @Embedded
    private LegalName legalName;

    @Column()
    @Getter
    @Embedded
    private CommercialName commercialName;

    @Column(nullable = false, updatable = false)
    @Getter
    @Embedded
    private Ruc ruc;

    @Column(nullable = false, updatable = false)
    @Getter


    @AttributeOverride(name = "personId", column = @Column(name = "created_by"))
    @Embedded
    private PersonId createdBy;

    @Getter
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "organization_status_id", nullable = false, unique = false)
    private OrganizationStatus status;

    @Getter
    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrganizationMember> members = new ArrayList<>();

    @Setter
    @Getter
    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrganizationInvitation> invitations = new ArrayList<>();


    protected Organization() {}

    /**
     * Constructor para crear una nueva Organización a partir de un comando.
     */
    public Organization(CreateOrganizationCommand command, OrganizationStatus status, OrganizationMemberType contractorType, ProfileDetails profileDetails) {
        this.legalName = new LegalName(command.legalName());
        this.commercialName = command.commercialName() != null ? new CommercialName(command.commercialName()) : new CommercialName(""); this.ruc = new Ruc(command.ruc());
        this.createdBy = new PersonId(command.createdBy());
        this.status = status;

        addContractor(command, contractorType, profileDetails);
    }

    /**
     * Actualiza la información de la organización.
     */
    public Organization updateInformation(String commercialName, String legalName){
        if(!commercialName.isBlank()) this.commercialName = new CommercialName(commercialName);
        if(!legalName.isBlank()) this.legalName = new LegalName(legalName);
        return this;
    }

    /**
     * Añade una nueva invitación a la organización.
     */
    public OrganizationInvitation addInvitation(PersonId personId, OrganizationInvitationStatus status) {
        if (isAlreadyMember(personId)) {
            throw new IllegalArgumentException("This person is already a member of the organization.");
        }

        if (hasPendingInvitationFor(personId)) {
            throw new IllegalArgumentException("There is already a pending invitation for this person.");
        }

        var invitation = new OrganizationInvitation(this, personId, status);
        invitations.add(invitation);

        return invitation;
    }

    /**
     * Acepta una invitación y añade al miembro.
     */
    public OrganizationInvitation acceptInvitation(Long invitationId, OrganizationInvitationStatus acceptedStatus, OrganizationMemberType memberType, ProfileDetails profileDetails) {
        OrganizationInvitation invitation = selectInvitationFromId(invitationId);

        if (!invitation.isPending()) {
            throw new IllegalStateException("Only pending invitations can be accepted");
        }

        invitation.accept(acceptedStatus);

        addMember(invitation, memberType, profileDetails);

        return invitation;
    }

    /**
     * Rechaza una invitación.
     */
    public OrganizationInvitation rejectInvitation(Long invitationId, OrganizationInvitationStatus rejectedStatus) {
        OrganizationInvitation invitation = selectInvitationFromId(invitationId);

        if (!invitation.isPending()) {
            throw new IllegalStateException("Only pending invitations can be accepted");
        }

        invitation.reject(rejectedStatus);

        return invitation;
    }

    /**
     * Añade al creador de la organización como el primer miembro (Contratista).
     */
    private void addContractor(CreateOrganizationCommand command, OrganizationMemberType contractorType, ProfileDetails profileDetails) {
        OrganizationMember member = new OrganizationMember(
                this,
                new PersonId(command.createdBy()),
                contractorType,
                profileDetails
        );

        members.add(member);
    }

    /**
     * Añade un nuevo miembro desde una invitación aceptada.
     */
    private void addMember(OrganizationInvitation invitation, OrganizationMemberType workerType, ProfileDetails profileDetails) {
        PersonId personId = invitation.getInvitedPersonId();

        if (isAlreadyMember(personId)) {
            throw new IllegalArgumentException("This person is already a member of the organization.");
        }

        OrganizationMember member = new OrganizationMember(this, personId, workerType, profileDetails);
        members.add(member);
    }

    /**
     * Elimina un miembro por su ID.
     */
    public void removeMemberById(Long memberId) {
        OrganizationMember member = members.stream()
                .filter(m -> m.getId().equals(memberId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No member found with ID: " + memberId));

        if (member.getMemberType().getName() == OrganizationMemberTypes.CONTRACTOR) {
            throw new IllegalArgumentException("Cannot remove member with role CONTRACTOR");
        }

        members.remove(member);
    }


    /**
     * Verifica si una persona ya es miembro.
     */
    private boolean isAlreadyMember(PersonId personId) {
        return members.stream()
                .anyMatch(member -> member.getPersonId().equals(personId));
    }

    /**
     * Verifica si una persona ya tiene una invitación pendiente.
     */
    private boolean hasPendingInvitationFor(PersonId personId) {
        return invitations.stream()
                .anyMatch(inv -> inv.isPending() && personId.equals(inv.getInvitedPersonId()));
    }

    /**
     * Busca una invitación por su ID.
     */
    public OrganizationInvitation selectInvitationFromId(Long invitationId) {
        return this.invitations.stream()
                .filter(i -> i.getId().equals(invitationId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Invitation not found in this organization"));
    }

    /**
     * Elimina invitaciones por el ID de la persona.
     */
    public void removeInvitationsByPersonId(PersonId personId) {
        invitations.removeIf(invitation -> invitation.getInvitedPersonId().equals(personId));
    }
}