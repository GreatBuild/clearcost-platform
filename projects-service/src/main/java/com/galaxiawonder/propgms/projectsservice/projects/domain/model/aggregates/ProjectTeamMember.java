package com.galaxiawonder.propgms.projectsservice.projects.domain.model.aggregates;


import com.galaxiawonder.propgms.projectsservice.projects.domain.model.commands.CreateProjectTeamMemberCommand;
import com.galaxiawonder.propgms.projectsservice.projects.domain.model.entities.ProjectTeamMemberType;
import com.galaxiawonder.propgms.projectsservice.projects.domain.model.entities.Specialty;
import com.galaxiawonder.propgms.projectsservice.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.galaxiawonder.propgms.projectsservice.shared.domain.model.valueobjects.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@NoArgsConstructor
@Entity
@Table(name = "project_team_members") // Añadimos @Table para claridad
@EntityListeners(AuditingEntityListener.class)
public class ProjectTeamMember extends AuditableAbstractAggregateRoot<ProjectTeamMember> {

    /**
     * Identifier of the project to which this member is assigned.
     */
    @Column(nullable = false, updatable = false)
    @AttributeOverride(name = "projectId", column = @Column(name = "project_id"))
    @Embedded
    private ProjectId projectId;

    /**
     * Specialty or expertise area of the assigned team member.
     */
    @Getter
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "specialty_id", nullable = false, unique = false)
    private Specialty specialty;

    /**
     * Identifier of the organization member assigned to this project.
     */
    @Column(nullable = false, updatable = false)

    @AttributeOverride(name = "organizationMemberId", column = @Column(name = "organization_member_id"))
    @Embedded
    private OrganizationMemberId organizationMemberId;

    /**
     * Unique identifier of the person associated with this team membership.
     */
    @Column(nullable = false, updatable = false)
    @AttributeOverride(name = "personId", column = @Column(name = "person_id"))
    @Embedded
    private PersonId personId;

    /**
     * Full name of the organization member, encapsulated in a value object.
     */
    @Getter
    @Embedded
    private PersonName name;

    /**
     * Unique email of the organization member, represented as a value object.
     */
    @Getter
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "address", column = @Column(name = "email"))})
    private EmailAddress email;

    /**
     * Current type of project team member (COORDINATOR / SPECIALIST)
     */
    @Getter
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "member_type_id", nullable = false)
    private ProjectTeamMemberType memberType;

    /**
     * Creates a new {@link ProjectTeamMember} with the given identifiers and personal information.
     *
     * @param organizationMemberId            the project to which the member is assigned
     */
    public ProjectTeamMember(Long projectId, Long organizationMemberId) {
        this.projectId = new ProjectId(projectId);
        this.organizationMemberId = new OrganizationMemberId(organizationMemberId);
    }

    public ProjectTeamMember(CreateProjectTeamMemberCommand command
    ) {
        this.projectId = new ProjectId(command.projectId());
        this.organizationMemberId = new OrganizationMemberId(command.organizationMemberId());
    }

    public void setPersonalInformation(PersonId personId, PersonName name, EmailAddress email) {
        this.personId = personId;
        this.name = name;
        this.email = email;
    }

    public void assignSpecialty(Specialty specialty) {
        this.specialty = specialty;
    }

    public void assignTeamMemberType(ProjectTeamMemberType memberType) {
        this.memberType = memberType;
    }
}