package com.galaxiawonder.propgms.projectsservice.projects.interfaces.rest.assemblers;


import com.galaxiawonder.propgms.projectsservice.projects.domain.model.aggregates.Project;
import com.galaxiawonder.propgms.projectsservice.projects.domain.model.commands.UpdateProjectCommand;
import com.galaxiawonder.propgms.projectsservice.projects.interfaces.rest.resources.UpdateProjectResource;

import java.util.Date;

public class UpdateProjectCommandFromResourceAssembler {



    public static UpdateProjectCommand toCommandFromResource(Long projectId, UpdateProjectResource resource) {
        if (projectId == null || projectId <= 0) {
            throw new IllegalArgumentException("projectId cannot be null or less than 1");
        }

        String name = (resource.name() == null) ? "" : resource.name();
        String description = (resource.description() == null) ? "" : resource.description();
        String status = (resource.status() == null) ? "" : resource.status();


        Date endingDate = (resource.endingDate() == null) ? Project.NO_UPDATE_DATE : resource.endingDate();

        return new UpdateProjectCommand(
                projectId,
                name,
                description,
                status,
                endingDate
        );
    }
}