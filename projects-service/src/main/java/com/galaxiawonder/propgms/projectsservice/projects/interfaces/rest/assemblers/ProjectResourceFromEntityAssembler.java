package com.galaxiawonder.propgms.projectsservice.projects.interfaces.rest.assemblers;


import com.galaxiawonder.propgms.projectsservice.projects.domain.model.aggregates.Project;
import com.galaxiawonder.propgms.projectsservice.projects.interfaces.rest.resources.ProjectResource;

public class ProjectResourceFromEntityAssembler {

    public static ProjectResource toResourceFromEntity(Project project) {
        return new ProjectResource(
                project.getId(),
                project.getProjectName().projectName(),
                project.getDescription().description(),
                project.getStatus().getStringName(), // Usamos getStringName()
                project.getDateRange().startDate(),
                project.getDateRange().endDate(),
                project.getOrganizationId().organizationId(),
                project.getContractingEntityId().personId()
        );
    }
}