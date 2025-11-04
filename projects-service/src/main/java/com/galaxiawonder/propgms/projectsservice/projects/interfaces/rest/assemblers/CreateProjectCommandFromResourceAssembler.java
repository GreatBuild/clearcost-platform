package com.galaxiawonder.propgms.projectsservice.projects.interfaces.rest.assemblers;


import com.galaxiawonder.propgms.projectsservice.projects.domain.model.commands.CreateProjectCommand;
import com.galaxiawonder.propgms.projectsservice.projects.interfaces.rest.resources.CreateProjectResource;

public class CreateProjectCommandFromResourceAssembler {

    public static CreateProjectCommand toCommandFromResource(CreateProjectResource resource) {
        return new CreateProjectCommand(
                resource.projectName(),
                resource.description(),
                resource.startDate(),
                resource.endDate(),
                resource.organizationId(),
                resource.contractingEntityEmail()
        );
    }
}