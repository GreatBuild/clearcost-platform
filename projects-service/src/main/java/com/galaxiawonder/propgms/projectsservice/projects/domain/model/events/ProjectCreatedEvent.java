package com.galaxiawonder.propgms.projectsservice.projects.domain.model.events;

import com.galaxiawonder.propgms.projectsservice.shared.domain.model.valueobjects.OrganizationId;
import com.galaxiawonder.propgms.projectsservice.shared.domain.model.valueobjects.ProjectId;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ProjectCreatedEvent extends ApplicationEvent {
    private final OrganizationId organizationId;
    private final ProjectId projectId;

    public ProjectCreatedEvent(Object source, OrganizationId organizationId, ProjectId projectId) {
        super(source);
        this.organizationId = organizationId;
        this.projectId = projectId;
    }
}