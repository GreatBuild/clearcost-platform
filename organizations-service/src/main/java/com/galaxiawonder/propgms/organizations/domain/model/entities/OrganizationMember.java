// Paquete actualizado para el nuevo microservicio
package com.galaxiawonder.propgms.organizations.domain.model.entities;


import com.galaxiawonder.propgms.organizations.domain.model.aggregates.Organization;
import com.galaxiawonder.propgms.organizations.shared.domain.model.entities.AuditableModel;
import com.galaxiawonder.propgms.organizations.shared.domain.model.valueobjects.EmailAddress;
import com.galaxiawonder.propgms.organizations.shared.domain.model.valueobjects.PersonId;
import com.galaxiawonder.propgms.organizations.shared.domain.model.valueobjects.PersonName;
import com.galaxiawonder.propgms.organizations.shared.domain.model.valueobjects.ProfileDetails;


import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Table(name = "organization_members")
@Entity
public class OrganizationMember extends AuditableModel {

    /** Identificador único de la persona asociada. */
    @Column(nullable = false, updatable = false)


    @AttributeOverride(name = "personId", column = @Column(name = "person_id"))
    @Embedded
    private PersonId personId;

    /** Nombre completo del miembro. */
    @Getter
    @Embedded
    private PersonName name;

    /** Email único del miembro. */
    @Getter
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "address", column = @Column(name = "email"))})
    private EmailAddress email;

    /** Organización a la que pertenece este miembro. */
    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    /** Tipo de miembro (rol). */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "member_type_id", nullable = false, unique = false)
    private OrganizationMemberType memberType;

    /** Constructor protegido por defecto para JPA. */
    public OrganizationMember() {}

    public OrganizationMember(Organization organization, PersonId personId, OrganizationMemberType workerType, ProfileDetails profileDetails) {
        this.organization = organization;
        this.personId = personId;
        this.memberType = workerType;
        this.name = profileDetails.name();
        this.email = profileDetails.email();
    }
}