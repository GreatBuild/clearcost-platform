// Paquete actualizado para el nuevo microservicio
package com.galaxiawonder.propgms.organizations.domain.model.entities;


import com.galaxiawonder.propgms.organizations.domain.model.aggregates.Organization;
import com.galaxiawonder.propgms.organizations.shared.domain.model.entities.AuditableModel;
import com.galaxiawonder.propgms.organizations.shared.domain.model.valueobjects.PersonId;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "organization_invitations") // Añadido nombre de tabla
public class OrganizationInvitation extends AuditableModel {

    /** Organización a la que pertenece esta invitación. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    /** ID de la persona invitada. */
    @Embedded
    // El 'AttributeOverride' original tenía "description", lo cambié por "personId"
    @AttributeOverride(name = "personId", column = @Column(name = "person_id", nullable = false, updatable = false))
    private PersonId invitedPersonId;

    /** Estado actual de la invitación. */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "status_id", nullable = false, unique = false)
    private OrganizationInvitationStatus status;

    public OrganizationInvitation(Organization organization, PersonId invitedPersonId, OrganizationInvitationStatus status) {
        this.organization = organization;
        this.invitedPersonId = invitedPersonId;
        this.status = status;
    }

    /** Marca la invitación como aceptada. */
    public void accept(OrganizationInvitationStatus acceptedStatus) {
        this.status = acceptedStatus;
    }

    /** Marca la invitación como rechazada. */
    public void reject(OrganizationInvitationStatus rejectedStatus) {
        this.status = rejectedStatus;
    }

    /** Verifica si la invitación está pendiente. */
    public boolean isPending() {
        // Comprobación más segura contra el enum
        return this.status.getName().equals(
                com.galaxiawonder.propgms.organizations.domain.model.valueobjects.OrganizationInvitationStatuses.PENDING
        );
    }
}