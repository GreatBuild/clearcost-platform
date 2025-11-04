package com.galaxiawonder.propgms.projectsservice.projects.domain.model.aggregates;


import com.galaxiawonder.propgms.projectsservice.projects.domain.model.commands.CreateProjectCommand;
import com.galaxiawonder.propgms.projectsservice.projects.domain.model.entities.ProjectStatus;
import com.galaxiawonder.propgms.projectsservice.projects.domain.model.events.ProjectCreatedEvent;
import com.galaxiawonder.propgms.projectsservice.projects.domain.model.valueobjects.DateRange;
import com.galaxiawonder.propgms.projectsservice.projects.domain.model.valueobjects.Description;
import com.galaxiawonder.propgms.projectsservice.projects.domain.model.valueobjects.ProjectName;
import com.galaxiawonder.propgms.projectsservice.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.galaxiawonder.propgms.projectsservice.shared.domain.model.valueobjects.*;




import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.*;

@Entity
@Table(name = "projects")
@EntityListeners(AuditingEntityListener.class)
public class Project extends AuditableAbstractAggregateRoot<Project> {


    /**
     * @apiNote
     * Se usa como valor "centinela" para indicar que no se debe actualizar la fecha.
     * Originalmente estaba en 'UpdateProjectCommandFromResourceAssembler',
     * pero se movió aquí para evitar que el Dominio dependa de la capa de Interfaces.
     */
    public static final Date NO_UPDATE_DATE = new GregorianCalendar(9999, Calendar.DECEMBER, 31).getTime();

    @Column(nullable = false)
    @Getter
    @Embedded
    private ProjectName projectName;

    @Column
    @Getter
    @Embedded
    private Description description;

    @Column(nullable = false)
    @Getter
    @Embedded
    private DateRange dateRange;

    @Column(nullable = false)
    @Getter
    @Embedded
    private OrganizationId organizationId;

    @Column(nullable = false)
    @Getter
    @Embedded
    private PersonId contractingEntityId;

    @Getter
    @Embedded
    private PersonName name;

    @Getter
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "address", column = @Column(name = "email"))})
    private EmailAddress email;

    @Getter
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "status_id", nullable = false, unique = false)
    private ProjectStatus status;

    @Getter
    private String previousStatusName;

    public Project() {}

    public Project(CreateProjectCommand command, ProjectStatus status, PersonId contractingEntityId, PersonName name, EmailAddress email) {
        this.projectName = new ProjectName(command.projectName());
        this.description = new Description(command.description());
        this.status = status;
        this.dateRange = new DateRange(command.startDate(), command.endDate());
        this.organizationId = new OrganizationId(command.organizationId());
        this.contractingEntityId = contractingEntityId;
        this.name = name;
        this.email = email;
    }

    public void updateInformation(String projectName, String description, ProjectStatus newStatus, Date newEndingDate) {
        if (projectName != null && !projectName.isBlank()) this.projectName = new ProjectName(projectName);
        if (description != null && !description.isBlank()) this.description = new Description(description);
        if (newStatus != null) this.status = newStatus;



        if (newEndingDate != null && !newEndingDate.equals(NO_UPDATE_DATE)) {
            this.dateRange = new DateRange(
                    this.getDateRange().startDate(), newEndingDate
            );
        }
    }

    public void reassignStatus(ProjectStatus newStatus) {
        this.recordPreviousStatus();
        if (newStatus == null) {
            throw new IllegalArgumentException("Project status cannot be null");
        }
        this.status = newStatus;
    }

    public void projectCreated() {

        if (this.getId() == null) {
            throw new IllegalStateException("Cannot create ProjectCreatedEvent before project is persisted");
        }
        this.registerEvent(new ProjectCreatedEvent(
                this,
                this.getOrganizationId(),
                new ProjectId(this.getId())) // Usa el ID del agregado
        );
    }

    void recordPreviousStatus() {
        if (this.status != null) {
            this.previousStatusName = this.status.getName().name();
        }
    }
}